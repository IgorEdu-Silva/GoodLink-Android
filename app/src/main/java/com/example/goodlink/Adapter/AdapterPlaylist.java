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
import com.example.goodlink.FireBaseManager.ManagerPlaylist;
import com.example.goodlink.PopUp.PopUpComment;
import com.example.goodlink.PopUp.PopUpDescription;
import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AdapterPlaylist extends RecyclerView.Adapter<AdapterPlaylist.PlaylistViewHolder> {
    private final List<ManagerPlaylist> playlists;
    private final AtomicReference<List<ManagerPlaylist>> playlistsFull = new AtomicReference<>();
    private static DatabaseReference databaseReference;
    private final Context context;
    private final Map<String, String> userIdToNameMap;
    private OnItemClickListener clickListener;
    private final FireStoreDataManager fireStoreDataManager;

    public AdapterPlaylist(List<ManagerPlaylist> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap) {
        this(playlists, databaseReference, context, userIdToNameMap, new FireStoreDataManager());
    }

    public AdapterPlaylist(List<ManagerPlaylist> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap, FireStoreDataManager fireStoreDataManager) {
        this.context = context;
        this.playlistsFull.set(new ArrayList<>(playlists));
        AdapterPlaylist.databaseReference = databaseReference;
        this.userIdToNameMap = userIdToNameMap;
        this.fireStoreDataManager = fireStoreDataManager != null ? fireStoreDataManager : new FireStoreDataManager();

        if (playlists != null) {
            this.playlists = playlists;
            this.playlistsFull.set(new ArrayList<>(this.playlists));
            for (ManagerPlaylist playlist : playlists) {
                if (playlist.getUserId() == null) {
                    Log.d(TAG, "UserID is null for playlist: " + playlist.getUserId());
                    playlist.setUserId("defaultValue");
                }
                if (playlist.getPlaylistId() == null) {
                    Log.d(TAG, "PlaylistID is null for playlist: " + playlist.getTitulo());
                    playlist.setPlaylistId("defaultValue");
                }
            }
        } else {
            this.playlists = new ArrayList<>();
            this.playlistsFull.set(new ArrayList<>());
        }
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_playlist_fragment, parent, false);
        return new PlaylistViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        if (position >= 0 && position < playlists.size()) {
            ManagerPlaylist playlist = playlists.get(position);

            if (playlist != null) {
                holder.bind(playlist);
                holder.tituloTextView.setText(playlist.getTitulo());
                holder.nomeCanalTextView.setText(playlist.getNomeCanal());

                String descricao = playlist.getDescricao();
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
                            clickListener.onItemClick(playlist.getUrlCanal());
                        }
                    }
                });

                holder.tituloTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onItemClick(playlist.getIframe());

                        }
                    }
                });

                ArrayAdapter<String> ratingAdapter = getStringArrayAdapter();
                holder.avaliacaoPlaylist.setAdapter(ratingAdapter);

                holder.avaliacaoPlaylist.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                holder.avaliacaoPlaylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            String rating = parent.getItemAtPosition(position).toString();
                            if (rating != null && !rating.isEmpty()) {
                                String userId = fireStoreDataManager.getCurrentUserId();
                                String playlistId = playlist.getPlaylistId();
                                Log.d(TAG, "UserId: " + userId + ", PlaylistId: " + playlistId);

                                if (userId != null && playlistId != null) {
                                     fireStoreDataManager.savePlaylistRating(userId, playlistId, rating, new FireStoreDataManager.OnPlaylistRatingSavedListener() {
                                        @Override
                                        public void onPlaylistRatingSaved(String savedPlaylistId) {
                                            Log.d(TAG, "Rating saved successfully for playlist: " + savedPlaylistId);
                                        }

                                        @Override
                                        public void onPlaylistRatingSaveFailed(String errorMessage) {
                                            Log.e(TAG, "Error saving rating for playlist: " + playlistId);
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

                holder.comentariosPlaylists.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUpCommentActivity(playlist.getPlaylistId());
                    }
                });

                holder.menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(holder.menuIcon, playlist);
                    }
                });
            }
        }
    }

    private void showPopupMenu(View view, ManagerPlaylist playlist) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_options_items_playlist);

        if (playlist.isFavorited()) {
            popupMenu.getMenu().findItem(R.id.favority).setTitle(R.string.remover_dos_favoritos);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.favority) {
                    favorityPlaylist(playlist);
                    return true;
                } else if (itemId == R.id.copy) {
                    return true;
                } else if (itemId == R.id.copyLinkCanal) {
                    copyLinkCanal(playlist.getPlaylistId());
                    return true;
                } else if (itemId == R.id.copyLinkPlaylist) {
                    copyLinkPlaylist(playlist.getPlaylistId());
                    return true;
                } else if (itemId == R.id.copyLinkBoth) {
                    copyLinkBoth(playlist.getPlaylistId());
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

    private void copyLinkBoth(String playlistId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksPlaylists(playlistId, new FireStoreDataManager.FireStoreDataListener<ManagerPlaylist>() {
            @Override
            public void onSuccess(ManagerPlaylist managerPlaylist) {
                String linkCanal = managerPlaylist.getUrlCanal();
                String linkPlaylist = managerPlaylist.getIframe();
                String textToCopy = "Link do canal: " + linkCanal + "\nLink da playlist: " + linkPlaylist;
                copyTextToClipboard("Ambos os links", textToCopy);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching playlist details: " + errorMessage);
            }
        });
    }

    private void copyLinkPlaylist(String playlistId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksPlaylists(playlistId, new FireStoreDataManager.FireStoreDataListener<ManagerPlaylist>() {
            @Override
            public void onSuccess(ManagerPlaylist managerPlaylist) {
                String linkPlaylist = managerPlaylist.getIframe();
                copyTextToClipboard("Link da playlist", linkPlaylist);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching playlist details: " + errorMessage);
            }
        });
    }

    private void copyLinkCanal(String playlistId) {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getLinksPlaylists(playlistId, new FireStoreDataManager.FireStoreDataListener<ManagerPlaylist>() {
            @Override
            public void onSuccess(ManagerPlaylist managerPlaylist) {
                String linkCanal = managerPlaylist.getUrlCanal();
                copyTextToClipboard("Link do canal", linkCanal);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error fetching playlist details: " + errorMessage);
            }
        });
    }

//    private void addPlaylist(View anchorView) {
//        Log.d("MainActivity", "addPlaylist called");
//        PopUpCreatePlaylist popupCreatePlaylist = new PopUpCreatePlaylist(context);
//        popupCreatePlaylist.show(anchorView);
//    }

    private void favorityPlaylist(ManagerPlaylist playlist) {
        String userId = fireStoreDataManager.getCurrentUserId();
        String playlistId = playlist.getPlaylistId();

        if (userId != null && playlistId != null) {
            if (playlist.isFavorited()) {
                fireStoreDataManager.removePlaylistFromFavorites(userId, playlistId, new FireStoreDataManager.OnPlaylistRemovedFromFavoritesListener() {
                    @Override
                    public void onPlaylistRemovedFromFavorites(String removedPlaylistId) {
                        Log.d(TAG, "Playlist removida dos favoritos: " + removedPlaylistId);
                        playlist.setFavorited(false);
                        notifyItemChanged(playlists.indexOf(playlist));
                    }

                    @Override
                    public void onPlaylistRemoveFromFavoritesFailed(String errorMessage) {
                        Log.e(TAG, "Erro ao remover playlist dos favoritos: " + errorMessage);
                    }
                });
            } else {
                fireStoreDataManager.addPlaylistToFavorites(userId, playlistId, new FireStoreDataManager.OnPlaylistAddedToFavoritesListener() {
                    @Override
                    public void onPlaylistAddedToFavorites(String addedPlaylistId) {
                        Log.d(TAG, "Playlist adicionada aos favoritos: " + addedPlaylistId);
                        playlist.setFavorited(true);
                        notifyItemChanged(playlists.indexOf(playlist));
                    }

                    @Override
                    public void onPlaylistAddToFavoritesFailed(String errorMessage) {
                        Log.e(TAG, "Erro ao adicionar playlist aos favoritos: " + errorMessage);
                    }
                });
            }
        } else {
            Log.e(TAG, "IDs de usuário ou playlist nulos");
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

    private void showPopUpCommentActivity(String playlistId){
        Intent intent = new Intent(context, PopUpComment.class);
        intent.putExtra("playlistId", playlistId);
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
        return playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tituloTextView;
        private final TextView descricaoTextView;
        private final TextView nomeCanalTextView;
        private final TextView nomeUsuarioTextView;
        private final TextView dataPubTextView;
        private final Spinner avaliacaoPlaylist;
        private final TextView comentariosPlaylists;
        private final ImageView menuIcon;


        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo_Playlist);
            descricaoTextView = itemView.findViewById(R.id.descricao_Playlist);
            nomeCanalTextView = itemView.findViewById(R.id.nomeCanal_Playlist);
            nomeUsuarioTextView = itemView.findViewById(R.id.nomeUsuario_Playlist);
            dataPubTextView = itemView.findViewById(R.id.dataPub_Playlist);
            avaliacaoPlaylist = itemView.findViewById(R.id.ratingBar);
            comentariosPlaylists = itemView.findViewById(R.id.comentariosPlaylists);
            menuIcon = itemView.findViewById(R.id.menuOptionsPlaylist);

        }

        public void bind(ManagerPlaylist managerPlaylist) {
            if (managerPlaylist != null) {
                tituloTextView.setText(managerPlaylist.getTitulo());
                descricaoTextView.setText(managerPlaylist.getDescricao());
                nomeCanalTextView.setText(managerPlaylist.getNomeCanal());

                String userId = managerPlaylist.getNomeUsuario();
                String userName = userIdToNameMap.get(userId);

                if (userName != null) {
                    nomeUsuarioTextView.setText(userName);
                } else {
                    nomeUsuarioTextView.setText(userId);
                }

                dataPubTextView.setText(managerPlaylist.getDataPub());
            }
        }
    }
}
