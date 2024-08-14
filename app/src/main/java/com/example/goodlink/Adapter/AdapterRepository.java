package com.example.goodlink.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.FireBaseManager.ManagerRepository;
import com.example.goodlink.PopUp.PopUpComment;
import com.example.goodlink.PopUp.PopUpDescription;
import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AdapterRepository extends RecyclerView.Adapter<AdapterRepository.RepositoryViewHolder> {
    private final List<ManagerRepository> repositories;
    private final AtomicReference<List<ManagerRepository>> repositoriesFull = new AtomicReference<>();
    private static DatabaseReference databaseReference;
    private final Context context;
    private final Map<String, String> userIdToNameMap;
    private OnItemClickListener clickListener;
    private final FireStoreDataManager fireStoreDataManager;

    public AdapterRepository(List<ManagerRepository> repositories, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap) {
        this(repositories, databaseReference, context, userIdToNameMap, new FireStoreDataManager());
    }

    public AdapterRepository(List<ManagerRepository> repositories, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap, FireStoreDataManager fireStoreDataManager) {
        this.context = context;
        this.repositoriesFull.set(new ArrayList<>(repositories));
        AdapterRepository.databaseReference = databaseReference;
        this.userIdToNameMap = userIdToNameMap;
        this.fireStoreDataManager = fireStoreDataManager != null ? fireStoreDataManager : new FireStoreDataManager();

        if (repositories != null) {
            this.repositories = repositories;
            this.repositoriesFull.set(new ArrayList<>(this.repositories));
            for (ManagerRepository repository : repositories) {
                if (repository.getUserId() == null) {
                    Log.d(TAG, "UserID is null for repository: " + repository.getUserId());
                    repository.setUserId("defaultValue");
                }
                if (repository.getRepositoryId() == null) {
                    Log.d(TAG, "repositoryID is null for repository: " + repository.getTitulo());
                    repository.setRepositoryId("defaultValue");
                }
            }
        } else {
            this.repositories = new ArrayList<>();
            this.repositoriesFull.set(new ArrayList<>());
        }
    }

    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_repository_fragment, parent, false);
        return new RepositoryViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {
        if (position >= 0 && position < repositories.size()) {
            ManagerRepository repository = repositories.get(position);

            if (repository != null) {
                holder.bind(repository);
                holder.tituloTextView.setText(repository.getTitulo());
                holder.nomeCanalTextView.setText(repository.getNomeCanal());

                String descricao = repository.getDescricao();
                if (descricao != null) {
                    if (descricao.length() > 40) {
                        SpannableString spannableString = getSpannableString(descricao);
                        holder.descricaoTextView.setText(spannableString);
                        holder.descricaoTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        holder.descricaoTextView.setText(descricao);
                        holder.descricaoTextView.setMovementMethod(null);
                    }
                } else {
                    holder.descricaoTextView.setText("");
                    holder.descricaoTextView.setMovementMethod(null);
                }

                holder.nomeCanalTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onItemClick(repository.getUrlCanal());
                        }
                    }
                });

                holder.tituloTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onItemClick(repository.getIframe());

                        }
                    }
                });

                ArrayAdapter<String> ratingAdapter = getStringArrayAdapter();
                holder.avaliacaoRepository.setAdapter(ratingAdapter);

                holder.avaliacaoRepository.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                holder.avaliacaoRepository.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            String rating = parent.getItemAtPosition(position).toString();
                            if (rating != null && !rating.isEmpty()) {
                                String userId = fireStoreDataManager.getCurrentUserId();
                                String repositoryId = repository.getRepositoryId();
                                Log.d(TAG, "UserId: " + userId + ", RepositoryId: " + repositoryId);

                                if (userId != null && repositoryId != null) {
                                     fireStoreDataManager.saveRepositoryRating(userId, repositoryId, rating, new FireStoreDataManager.OnRepositoryRatingSavedListener() {
                                        @Override
                                        public void onRepositoryRatingSaved(String repositoryId) {
                                            Log.d(TAG, "Rating saved successfully for repository: " + repositoryId);
                                        }

                                        @Override
                                        public void onRepositoryRatingSaveFailed(String errorMessage) {
                                            Log.e(TAG, "Error saving rating for repository: " + repositoryId);
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "One or more required IDs are null");
                                }
                            } else {
                                Log.e(TAG, "Rating is null or empty");
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                holder.comentariosRepositories.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUpCommentActivity(repository.getRepositoryId());
                    }
                });

                holder.menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(holder.menuIcon, repository);
                    }
                });
            }
        }
    }

    private void showPopupMenu(View view, ManagerRepository repository) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_options_items_repository);

        if (repository.isFavorited()) {
            popupMenu.getMenu().findItem(R.id.favority).setTitle(R.string.remover_dos_favoritos);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.favority) {
                    favorityRepository(repository);
                    return true;
                } else if (itemId == R.id.copy) {
                    return true;
                } else if (itemId == R.id.copyLinkCanal) {
                    copyLinkCanal(repository.getRepositoryId());
                    return true;
                } else if (itemId == R.id.copyLinkRepository) {
                    copyLinkRepository(repository.getRepositoryId());
                    return true;
                } else if (itemId == R.id.copyLinkBoth) {
                    copyLinkBoth(repository.getRepositoryId());
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void copyTextToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, label + " copiado para a área de transferência.", Toast.LENGTH_SHORT).show();
    }

    private void copyLinkBoth(String repositoryId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksRepositories(repositoryId, new FireStoreDataManager.FireStoreDataListener<ManagerRepository>() {
            @Override
            public void onSuccess(ManagerRepository managerRepository) {
                String linkCanal = managerRepository.getUrlCanal();
                String linkRepository = managerRepository.getIframe();
                String textToCopy = "Link do criador: " + linkCanal + "\nLink do repositório: " + linkRepository;
                copyTextToClipboard("Ambos os links", textToCopy);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching repository details: " + errorMessage);
            }
        });
    }

    private void copyLinkRepository(String repositoryId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksRepositories(repositoryId, new FireStoreDataManager.FireStoreDataListener<ManagerRepository>() {
            @Override
            public void onSuccess(ManagerRepository managerRepository) {
                String linkRepository = managerRepository.getIframe();
                copyTextToClipboard("Link do repositório", linkRepository);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching repository details: " + errorMessage);
            }
        });
    }

    private void copyLinkCanal(String repositoryId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksRepositories(repositoryId, new FireStoreDataManager.FireStoreDataListener<ManagerRepository>() {
            @Override
            public void onSuccess(ManagerRepository managerRepository) {
                String linkCanal = managerRepository.getUrlCanal();
                copyTextToClipboard("Link do canal", linkCanal);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching repository details: " + errorMessage);
            }
        });
    }

//    private void addRepository (View anchorView) {
//        Log.d("MainActivity", "addRepository called");
//        PopUpCreateRepository popupCreateRepository = new PopUpCreateRepository(context);
//        popupCreateRepository.show(anchorView);
//    }

    private void favorityRepository(ManagerRepository repository) {
        String userId = fireStoreDataManager.getCurrentUserId();
        String repositoryId = repository.getRepositoryId();

        if (userId != null && repositoryId != null) {
            if (repository.isFavorited()) {
                fireStoreDataManager.removeRepositoryFromFavorites(userId, repositoryId, new FireStoreDataManager.OnRepositoryRemovedFromFavoritesListener() {
                    @Override
                    public void onRepositoryRemovedFromFavorites(String removedRepositoryId) {
                        Log.d(TAG, "Repositório removida dos favoritos: " + removedRepositoryId);
                        repository.setFavorited(false);
                        notifyItemChanged(repositories.indexOf(repository));
                    }

                    @Override
                    public void onRepositoryRemoveFromFavoritesFailed(String errorMessage) {
                        Log.e(TAG, "Erro ao remover repository dos favoritos: " + errorMessage);
                    }
                });
            } else {
                fireStoreDataManager.addRepositoryToFavorites(userId, repositoryId, new FireStoreDataManager.OnRepositoryAddedToFavoritesListener() {
                    @Override
                    public void onRepositoryAddedToFavorites(String addedRepositoryId) {
                        Log.d(TAG, "Repositório adicionado aos favoritos: " + addedRepositoryId);
                        repository.setFavorited(true);
                        notifyItemChanged(repositories.indexOf(repository));
                    }

                    @Override
                    public void onRepositoryAddToFavoritesFailed(String errorMessage) {
                        Log.e(TAG, "Erro ao adicionar repository aos favoritos: " + errorMessage);
                    }
                });
            }
        } else {
            Log.e(TAG, "IDs de usuário ou repositório nulos");
        }
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        List<String> ratingOptions = new ArrayList<>();

        ratingOptions.add("Péssimo");
        ratingOptions.add("Ruim");
        ratingOptions.add("Regular");
        ratingOptions.add("Bom");
        ratingOptions.add("Excelente");

        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ratingOptions);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return ratingAdapter;
    }

    @NonNull
    private SpannableString getSpannableString(String descricao) {
        String descricaoResumida = descricao.substring(0, 40) + "... Ver mais";
        SpannableString spannableString = new SpannableString(descricaoResumida);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openPopUp(descricao);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#0099DD"));

            }
        };

        spannableString.setSpan(clickableSpan, descricaoResumida.length() - 8, descricaoResumida.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void openPopUp(String fullDescription) {
        PopUpDescription popUpDescription = PopUpDescription.newInstance(fullDescription);
        popUpDescription.show(((FragmentActivity) context).getSupportFragmentManager(), "pop_up_verMais");
    }

    private void showPopUpCommentActivity(String repositoryId){
        Intent intent = new Intent(context, PopUpComment.class);
        intent.putExtra("repositoryId", repositoryId);
        context.startActivity(intent);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    public class RepositoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tituloTextView;
        private final TextView descricaoTextView;
        private final TextView nomeCanalTextView;
        private final TextView nomeUsuarioTextView;
        private final TextView dataPubTextView;
        private final Spinner avaliacaoRepository;
        private final TextView comentariosRepositories;
        private final ImageView menuIcon;


        public RepositoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo_Repository);
            descricaoTextView = itemView.findViewById(R.id.descricao_Repository);
            nomeCanalTextView = itemView.findViewById(R.id.nomeCanal_Repository);
            nomeUsuarioTextView = itemView.findViewById(R.id.nomeUsuario_Repository);
            dataPubTextView = itemView.findViewById(R.id.dataPub_Repository);
            avaliacaoRepository = itemView.findViewById(R.id.ratingBar);
            comentariosRepositories = itemView.findViewById(R.id.commentsRepositories);
            menuIcon = itemView.findViewById(R.id.menuOptionsRepository);

        }

        public void bind(ManagerRepository managerRepository) {
            if (managerRepository != null) {
                tituloTextView.setText(managerRepository.getTitulo());
                descricaoTextView.setText(managerRepository.getDescricao());
                nomeCanalTextView.setText(managerRepository.getNomeCanal());

                String userId = managerRepository.getNomeUsuario();
                String userName = userIdToNameMap.get(userId);

                if (userName != null) {
                    nomeUsuarioTextView.setText(userName);
                } else {
                    nomeUsuarioTextView.setText(userId);
                }

                dataPubTextView.setText(managerRepository.getDataPub());
            }
        }
    }
}
