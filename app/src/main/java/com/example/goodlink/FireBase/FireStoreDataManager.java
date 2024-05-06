package com.example.goodlink.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
        if (userId != null && !userId.isEmpty()) {
            Map<String, Object> playlist = getStringObjectMap(playlistData);

            playlistsCollection.add(playlist)
                    .addOnSuccessListener(documentReference -> {
                        String playlistId = documentReference.getId();
                        playlistData.setPlaylistId(playlistId);
                        playlistData.setUserId(userId);
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

    public void savePlaylistRating(String userId, String playlistId, String rating, OnPlaylistRatingSavedListener listener) {
        if (userId != null && playlistId != null && rating != null) {
            RatingManager ratingManager = new RatingManager(rating, userId, playlistId);

            usersCollection.document(userId).collection("userRatings").document(playlistId).set(ratingManager)
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

    public interface OnPlaylistRatingSavedListener {

        void onPlaylistRatingSaved(String playlistId);

        void onPlaylistRatingSaveFailed(String errorMessage);
    }
}
