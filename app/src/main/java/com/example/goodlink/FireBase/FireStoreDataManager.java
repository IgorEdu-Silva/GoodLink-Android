package com.example.goodlink.FireBase;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.goodlink.Fragments.PlaylistData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireStoreDataManager {
    private final FirebaseFirestore firestore;
    private final CollectionReference usersCollection;
    private final CollectionReference playlistsCollection;

    public FireStoreDataManager() {
        firestore = FirebaseFirestore.getInstance();
        usersCollection = firestore.collection("users");
        playlistsCollection = firestore.collection("playlists");
    }

    public void addUser(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        usersCollection.document(userId).set(user)
                .addOnSuccessListener(aVoid -> Log.d("FireStoreDataManager", "User added successfully"))
                .addOnFailureListener(e -> Log.e("FireStoreDataManager", "Error adding user", e));
    }

    public void getUser(String userId, final FireStoreDataListener<User> listener) {
        usersCollection.document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User userData = documentSnapshot.toObject(User.class);
                        listener.onSuccess(userData);
                    } else {
                        listener.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    private static final String TAG = "FireStoreDataManager";

    public void getPlaylistsFromFirestore(OnPlaylistsLoadedListener listener) {
        playlistsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PlaylistData> playlists = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        PlaylistData playlistData = documentSnapshot.toObject(PlaylistData.class);
                        playlists.add(playlistData);
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

    public void createPlaylist(String userId, PlaylistData playlistData, OnPlaylistCreatedListener listener) {
        Map<String, Object> playlist = getStringObjectMap(playlistData);

        playlistsCollection.add(playlist)
                .addOnSuccessListener(documentReference -> {
                    listener.onPlaylistCreated(documentReference.getId());
                    usersCollection.document(userId).update("playlistId", documentReference.getId())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Playlist ID updated for user: " + userId))
                            .addOnFailureListener(e -> Log.e(TAG, "Error updating playlist ID for user: " + userId, e));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating playlist: ", e);
                    listener.onPlaylistCreationFailed(e.getMessage());
                });
    }

    @NonNull
    private static Map<String, Object> getStringObjectMap(PlaylistData playlistData) {
        Map<String, Object> playlist = new HashMap<>();
        playlist.put("titulo", playlistData.getTitulo());
        playlist.put("descricao", playlistData.getDescricao());
        playlist.put("nomeCanal", playlistData.getNomeCanal());
        playlist.put("iframe", playlistData.getIframe());
        playlist.put("urlCanal", playlistData.getUrlCanal());
        playlist.put("categoria", playlistData.getCategoria());
        playlist.put("nomeUsuario", playlistData.getNomeUsuario());
        playlist.put("dataPub", playlistData.getDataPub());
        return playlist;
    }

    public void savePlaylistRating(String userId, PlaylistData playlistData, String rating, OnPlaylistRatingSavedListener listener) {
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("avaliacao", rating);

        playlistsCollection.add(getStringObjectMap(playlistData))
                .addOnSuccessListener(documentReference -> {
                    String playlistId = documentReference.getId();
                    listener.onPlaylistRatingSaved(playlistId); // Notificar o ID da playlist
                    CollectionReference userPlaylistRef = usersCollection.document(userId).collection("userPlaylists");
                    userPlaylistRef.document(playlistId).set(ratingData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Rating saved successfully"))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error saving rating", e);
                                listener.onPlaylistRatingSaveFailed(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating playlist: ", e);
                    listener.onPlaylistRatingSaveFailed(e.getMessage());
                });
    }

    public void loadPlaylistRating(String userId, String playlistId, FireStoreDataListener<String> listener) {
        CollectionReference userPlaylistRef = usersCollection.document(userId).collection("userPlaylists");
        userPlaylistRef.document(playlistId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rating = documentSnapshot.getString("avaliacao");
                        listener.onSuccess(rating);
                    } else {
                        listener.onFailure("Rating not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnPlaylistRatingSavedListener {
        void onPlaylistRatingSaved(String playlistId);
        void onPlaylistRatingSaveFailed(String errorMessage);
    }

    public interface OnUserIdToNameMapListener {
        void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap);
        void onUserIdToNameMapLoadFailed(String errorMessage);
    }

    public interface OnPlaylistsLoadedListener {
        void onPlaylistsLoaded(List<PlaylistData> playlists);
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

}
