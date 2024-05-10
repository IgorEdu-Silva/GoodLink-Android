    package com.example.goodlink.Fragments;

    import static android.content.ContentValues.TAG;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.graphics.Color;
    import android.text.SpannableString;
    import android.text.Spanned;
    import android.text.TextPaint;
    import android.text.method.LinkMovementMethod;
    import android.text.style.ClickableSpan;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.Spinner;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.FragmentActivity;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.goodlink.FireBase.FireStoreDataManager;
    import com.example.goodlink.FireBase.PlaylistData;
    import com.example.goodlink.FireBase.RatingManager;
    import com.example.goodlink.Functions.PopUp;
    import com.example.goodlink.R;
    import com.google.firebase.database.DatabaseReference;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.atomic.AtomicReference;

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
        private final List<PlaylistData> playlists;
        private final AtomicReference<List<PlaylistData>> playlistsFull = new AtomicReference<>();
        private static DatabaseReference databaseReference;
        private final Context context;
        private final Map<String, String> userIdToNameMap;
        private OnItemClickListener clickListener;
        private final FireStoreDataManager fireStoreDataManager;

        public PlaylistAdapter(List<PlaylistData> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap) {
            this(playlists, databaseReference, context, userIdToNameMap, new FireStoreDataManager());
        }

        public PlaylistAdapter(List<PlaylistData> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap, FireStoreDataManager fireStoreDataManager) {
            this.playlists = playlists;
            this.context = context;
            this.playlistsFull.set(new ArrayList<>(playlists));
            PlaylistAdapter.databaseReference = databaseReference;
            this.userIdToNameMap = userIdToNameMap;
            this.fireStoreDataManager = fireStoreDataManager != null ? fireStoreDataManager : new FireStoreDataManager();

            for (PlaylistData playlist : playlists) {
                if (playlist.getUserId() == null) {
                    Log.d(TAG, "UserID is null for playlist: " + playlist.getUserId());
                    playlist.setUserId("defaultValue");
                }
                if (playlist.getPlaylistId() == null) {
                    Log.d(TAG, "PlaylistID is null for playlist: " + playlist.getPlaylistId());
                    playlist.setPlaylistId("defaultValue");
                }
            }
        }

        private String getPlaylistIdByTitle(String playlistTitle) {
            for (PlaylistData playlist : playlists) {
                if (playlist.getTitulo().equals(playlistTitle)) {
                    return playlist.getPlaylistId();
                }
            }
            return null;
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
                PlaylistData playlist = playlists.get(position);

                if (playlist != null) {
                    holder.bind(playlist);
                    holder.tituloTextView.setText(playlist.getTitulo());
                    holder.nomeCanalTextView.setText(playlist.getNomeCanal());

                    String descricao = playlist.getDescricao();
                    if (descricao != null) {
                        if (descricao.length() > 48) {
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

                    playlist.setUserId(fireStoreDataManager.getCurrentUserId());

                    Log.d(TAG, "UserID = " + playlist.getUserId());
                    Log.d(TAG, "PlaylistID = " + playlist.getPlaylistId());

                    String userId = playlist.getUserId();
                    String rating = ratingAdapter.getItem(holder.avaliacaoPlaylist.getSelectedItemPosition());

                    if (userId != null && playlist != null) {
                        saveOrUpdateUserRating(userId, playlist, rating);
                        loadUserRating(holder, userId, playlist);
                    } else {
                        Log.e(TAG, "Error: UserID is null for playlist: " + playlist.getTitulo());
                    }
                }
            }
        }

        private void saveOrUpdateUserRating(String userId, PlaylistData playlist, String rating) {
            if (userId != null && !userId.isEmpty() && playlist != null && rating != null && !rating.isEmpty()) {
                playlist.setUserId(userId);

                String playlistId = playlist.getPlaylistId();
                if (playlistId != null && !playlistId.isEmpty()) {
                    fireStoreDataManager.getUserRating(userId, playlistId, new FireStoreDataManager.OnPlaylistRatingLoadedListener() {
                        @Override
                        public void onPlaylistRatingLoaded(RatingManager ratingManager, String playlistId) {
                            if (ratingManager != null) {
                                fireStoreDataManager.updateUserRating(userId, playlist, rating, new FireStoreDataManager.OnPlaylistRatingUpdatedListener() {
                                    @Override
                                    public void onPlaylistRatingUpdated() {
                                        Log.d(TAG, "Rating updated successfully for user: " + userId);
                                    }

                                    @Override
                                    public void onPlaylistRatingUpdateFailed(String errorMessage) {
                                        Log.e(TAG, "Error updating rating for user: " + userId);
                                    }
                                });
                            } else {
                                saveUserRating(userId, playlistId, rating);
                            }
                        }

                        @Override
                        public void onPlaylistRatingLoadFailed(String errorMessage) {
                            Log.e(TAG, errorMessage);
                        }
                    });
                } else {
                    Log.e(TAG, "Playlist ID is null or empty");
                }
            } else {
                Log.e(TAG, "One or more required fields are null");
            }
        }

        private void saveUserRating(String userId, String playlistId, String rating) {
            fireStoreDataManager.saveOrUpdateUserRating(userId, playlistId, rating, new FireStoreDataManager.OnPlaylistRatingSavedListener() {
                @Override
                public void onPlaylistRatingSaved(String playlistId) {
                    Log.d(TAG, "Rating saved successfully for user: " + userId);
                }

                @Override
                public void onPlaylistRatingSaveFailed(String errorMessage) {
                    Log.e(TAG, "Error saving rating for user: " + userId);
                }
            });
        }

        private void loadUserRating(PlaylistViewHolder holder, String userId, PlaylistData playlist) {
            if (userId != null && !userId.isEmpty() && playlist != null) {
                fireStoreDataManager.getUserRating(userId, playlist.getPlaylistId(), new FireStoreDataManager.OnPlaylistRatingLoadedListener() {
                    @Override
                    public void onPlaylistRatingLoaded(RatingManager ratingManager, String playlistId) {
                        if (ratingManager != null) {
                            holder.RatingOnOff.setTextColor(Color.BLUE);
                        }
                    }

                    @Override
                    public void onPlaylistRatingLoadFailed(String errorMessage) {
                        Log.e(TAG, errorMessage);
                    }
                });
            } else {
                Log.e(TAG, "One or more required fields are null");
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
            String descricaoResumida = descricao.substring(0, 48) + "... Ver mais";
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
                    ds.setColor(Color.parseColor("#0078bd"));

                }
            };

            spannableString.setSpan(clickableSpan, descricaoResumida.length() - 8, descricaoResumida.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        private void openPopUp(String fullDescription) {
            PopUp popUp = PopUp.newInstance(fullDescription);
            popUp.show(((FragmentActivity) context).getSupportFragmentManager(), "pop_up_verMais");
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
            private final TextView RatingOnOff;

            public PlaylistViewHolder(@NonNull View itemView) {
                super(itemView);
                tituloTextView = itemView.findViewById(R.id.titulo_Playlist);
                descricaoTextView = itemView.findViewById(R.id.descricao_Playlist);
                nomeCanalTextView = itemView.findViewById(R.id.nomeCanal_Playlist);
                nomeUsuarioTextView = itemView.findViewById(R.id.nomeUsuario_Playlist);
                dataPubTextView = itemView.findViewById(R.id.dataPub_Playlist);
                avaliacaoPlaylist = itemView.findViewById(R.id.ratingBar);
                RatingOnOff = itemView.findViewById(R.id.RatingOnOff);
            }

            public void bind(PlaylistData playlistData) {
                if (playlistData != null) {
                    tituloTextView.setText(playlistData.getTitulo());
                    descricaoTextView.setText(playlistData.getDescricao());
                    nomeCanalTextView.setText(playlistData.getNomeCanal());

                    String userId = playlistData.getNomeUsuario();
                    String userName = userIdToNameMap.get(userId);

                    if (userName != null) {
                        nomeUsuarioTextView.setText(userName);
                    } else {
                        nomeUsuarioTextView.setText(userId);
                    }

                    dataPubTextView.setText(playlistData.getDataPub());
                }
            }
        }
    }
