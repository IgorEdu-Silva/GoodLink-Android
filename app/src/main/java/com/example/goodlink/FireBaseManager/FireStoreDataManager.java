    package com.example.goodlink.FireBaseManager;

    import android.annotation.SuppressLint;
    import android.util.Log;

    import androidx.annotation.NonNull;

    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class FireStoreDataManager {
        private final FirebaseFirestore firestore;
        private final CollectionReference usersCollection;
        private final CollectionReference playlistsCollection;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();
        private final FirebaseAuth firebaseAuth;

        public FireStoreDataManager() {
            firestore = FirebaseFirestore.getInstance();
            usersCollection = firestore.collection("users");
            playlistsCollection = firestore.collection("playlists");
            firebaseAuth = FirebaseAuth.getInstance();
        }

        public DocumentReference getUserDocumentReference(String userId) {
            return db.collection("users").document(userId);
        }

        public DocumentReference getPlaylistDocumentReference(String playlistId) {
            return db.collection("playlists").document(playlistId);
        }

        public Task<DocumentSnapshot> getPlaylistData(String playlistId) {
            return db.collection("playlists").document(playlistId).get();
        }

        public Task<DocumentSnapshot> getUserData(String userId) {
            return db.collection("users").document(userId).get();
        }

        public void addUser(String userId, String name, String email) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);

            usersCollection.document(userId).set(user)
                    .addOnSuccessListener(aVoid -> Log.d("FireStoreDataManager", "ManagerUser added successfully"))
                    .addOnFailureListener(e -> Log.e("FireStoreDataManager", "Error adding user", e));
        }

        public void getUser(String userId, final FireStoreDataListener<ManagerUser> listener) {
            usersCollection.document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            ManagerUser managerUserData = documentSnapshot.toObject(ManagerUser.class);
                            listener.onSuccess(managerUserData);
                        } else {
                            listener.onFailure("ManagerUser not found");
                        }
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }

        private static final String TAG = "FireStoreDataManager";

        public String getCurrentUserId() {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                return currentUser.getUid();
            } else {
                Log.e(TAG, "Current user is null");
                return null;
            }
        }

        public String generatePlaylistId() {
            return playlistsCollection.document().getId();
        }

        public void getPlaylistsFromFirestore(OnPlaylistsLoadedListener listener) {
            playlistsCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<ManagerPlaylist> playlists = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ManagerPlaylist managerPlaylist = documentSnapshot.toObject(ManagerPlaylist.class);
                            managerPlaylist.setPlaylistId(documentSnapshot.getId());
                            playlists.add(managerPlaylist);
                        }
                        listener.onPlaylistsLoaded(playlists);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching playlists: ", e);
                        listener.onPlaylistsLoadFailed(e.getMessage());
                    });
        }

        public void getUserIdToNameMap(OnUserIdToNameMapListener listener) {
            usersCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Map<String, String> userIdToNameMap = new HashMap<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String userId = documentSnapshot.getId();
                            String userName = documentSnapshot.getString("name");
                            userIdToNameMap.put(userId, userName);
                        }
                        listener.onUserIdToNameMapLoaded(userIdToNameMap);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user IDs and names: ", e);
                        listener.onUserIdToNameMapLoadFailed(e.getMessage());
                    });
        }

        public void createPlaylist(String userId, ManagerPlaylist managerPlaylist, OnPlaylistCreatedListener listener) {
            if (userId != null && !userId.isEmpty()) {
                Map<String, Object> playlist = getStringObjectMap(managerPlaylist);

                playlistsCollection.add(playlist)
                        .addOnSuccessListener(documentReference -> {
                            String playlistId = documentReference.getId();
                            managerPlaylist.setPlaylistId(playlistId);
                            managerPlaylist.setUserId(userId);
                            listener.onPlaylistCreated(playlistId);
                            Map<String, Object> playlistIdMap = new HashMap<>();
                            playlistIdMap.put("playlistId", playlistId);
                            usersCollection.document(userId).collection("userPlaylists").add(playlistIdMap)
                                    .addOnSuccessListener(documentReference1 -> Log.d(TAG, "Playlist ID added to user's collection: " + userId))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding playlist ID to user's collection: " + userId, e));
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error creating playlist: ", e);
                            listener.onPlaylistCreationFailed(e.getMessage());
                        });
            } else {
                Log.e(TAG, "Error creating playlist: UserId is null or empty");
                listener.onPlaylistCreationFailed("UserId is null or empty");
            }
        }

        @NonNull
        private static Map<String, Object> getStringObjectMap(ManagerPlaylist managerPlaylist) {
            Map<String, Object> playlist = new HashMap<>();
            playlist.put("titulo", managerPlaylist.getTitulo());
            playlist.put("descricao", managerPlaylist.getDescricao());
            playlist.put("nomeCanal", managerPlaylist.getNomeCanal());
            playlist.put("iframe", managerPlaylist.getIframe());
            playlist.put("urlCanal", managerPlaylist.getUrlCanal());
            playlist.put("categoria", managerPlaylist.getCategoria());
            playlist.put("nomeUsuario", managerPlaylist.getNomeUsuario());
            playlist.put("dataPub", managerPlaylist.getDataPub());
            return playlist;
        }

        public void savePlaylistRating(String userId, String playlistId, String rating, OnPlaylistRatingSavedListener listener) {
            if (userId != null && playlistId != null && rating != null) {
                DocumentReference userRatingRef = usersCollection.document(userId).collection("userRatings").document(playlistId);

                Map<String, Object> newRating = new HashMap<>();
                newRating.put("rating", rating);
                newRating.put("userRating", userId);
                newRating.put("playlistRated", playlistId);

                userRatingRef.set(newRating)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Rating saved successfully for playlist: " + playlistId);
                            listener.onPlaylistRatingSaved(playlistId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error saving rating for playlist: " + playlistId, e);
                            listener.onPlaylistRatingSaveFailed(e.getMessage());
                        });
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onPlaylistRatingSaveFailed("One or more required fields are null");
            }
        }

        public void saveUserComment(String commentText, String playlistId, String userName, OnCommentSavedListener listener) {
            String commentId = db.collection("userComments").document().getId();
            ManagerComment comment = new ManagerComment(userName, commentText, playlistId);

            db.collection("userComments")
                    .document(commentId)
                    .set(comment)
                    .addOnSuccessListener(aVoid -> listener.onCommentSaved())
                    .addOnFailureListener(e -> listener.onCommentSaveFailed(e.getMessage()));
        }

        public void getCommentsByPlaylistId(String playlistId, OnCommentsLoadedListener listener) {
            db.collectionGroup("userComments")
                    .whereEqualTo("playlistId", playlistId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<ManagerComment> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ManagerComment comment = document.toObject(ManagerComment.class);
                                comments.add(comment);
                            }
                            listener.onCommentsLoaded(comments);
                        } else {
                            listener.onCommentsLoadFailed(task.getException().getMessage());
                        }
                    });
        }

        public void getLinksPlaylists(String playlistId, FireStoreDataListener<ManagerPlaylist> listener) {
            playlistsCollection.document(playlistId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            ManagerPlaylist managerPlaylist = documentSnapshot.toObject(ManagerPlaylist.class);
                            listener.onSuccess(managerPlaylist);
                        } else {
                            listener.onFailure("Playlist not found");
                        }
                    })
                    .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }

        public void removePlaylistFromFavorites(String userId, String playlistId, OnPlaylistRemovedFromFavoritesListener listener) {
            if (userId != null && playlistId != null) {
                usersCollection.document(userId)
                        .collection("userFavorites")
                        .document(playlistId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Playlist removed from favorites for user: " + userId);
                            listener.onPlaylistRemovedFromFavorites(playlistId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error removing playlist from favorites for user: " + userId, e);
                            listener.onPlaylistRemoveFromFavoritesFailed(e.getMessage());
                        });

                playlistsCollection.document(playlistId)
                        .update("favorited", false)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Playlist updated as not favorited: " + playlistId))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating playlist as not favorited: " + playlistId, e));
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onPlaylistRemoveFromFavoritesFailed("One or more required fields are null");
            }
        }

        public void addPlaylistToFavorites(String userId, String playlistId, OnPlaylistAddedToFavoritesListener listener) {
            if (userId != null && playlistId != null) {
                Map<String, Object> playlistIdMap = new HashMap<>();
                playlistIdMap.put("playlistId", playlistId);

                usersCollection.document(userId)
                        .collection("userFavorites")
                        .document(playlistId)
                        .set(playlistIdMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Playlist added to favorites for user: " + userId);
                            listener.onPlaylistAddedToFavorites(playlistId);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding playlist to favorites for user: " + userId, e);
                            listener.onPlaylistAddToFavoritesFailed(e.getMessage());
                        });

                playlistsCollection.document(playlistId)
                        .update("favorited", true)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Playlist updated as favorited: " + playlistId))
                        .addOnFailureListener(e -> Log.e(TAG, "Error updating playlist as favorited: " + playlistId, e));
            } else {
                Log.e(TAG, "One or more required fields are null");
                listener.onPlaylistAddToFavoritesFailed("One or more required fields are null");
            }
        }

        public interface OnCommentSavedListener {
            void onCommentSaved();
            void onCommentSaveFailed(String errorMessage);
        }


        public interface OnUserIdToNameMapListener {
            void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap);
            void onUserIdToNameMapLoadFailed(String errorMessage);
        }

        public interface OnPlaylistsLoadedListener {
            void onPlaylistsLoaded(List<ManagerPlaylist> playlists);
            void onPlaylistsLoadFailed(String errorMessage);
        }

        public interface OnPlaylistCreatedListener {
            void onPlaylistCreated(String playlistId);
            void onPlaylistCreationFailed(String errorMessage);
        }

        public interface FireStoreDataListener<T> {
            void onSuccess(T data);
            void onFailure(String errorMessage);
        }

        public interface OnPlaylistRatingSavedListener {
            void onPlaylistRatingSaved(String playlistId);
            void onPlaylistRatingSaveFailed(String errorMessage);
        }

        public interface OnCommentsLoadedListener {
            @SuppressLint("NotifyDataSetChanged")
            void onCommentsLoaded(List<ManagerComment> comments);

            void onCommentsLoadFailed(String errorMessage);
        }

        public interface OnPlaylistRemovedFromFavoritesListener {
            void onPlaylistRemovedFromFavorites(String removedPlaylistId);

            void onPlaylistRemoveFromFavoritesFailed(String errorMessage);
        }

        public interface OnPlaylistAddedToFavoritesListener {
            void onPlaylistAddedToFavorites(String addedPlaylistId);

            void onPlaylistAddToFavoritesFailed(String errorMessage);
        }
    }
