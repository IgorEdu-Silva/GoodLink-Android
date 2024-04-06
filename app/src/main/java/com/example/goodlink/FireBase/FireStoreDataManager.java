package com.example.goodlink.FireBase;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.goodlink.Fragments.PlaylistData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FireStoreDataManager {
    private final FirebaseFirestore firestore;
    private final CollectionReference usersCollection;

    public FireStoreDataManager() {
        firestore = FirebaseFirestore.getInstance();
        usersCollection = firestore.collection("users");
    }

    public void addUser(String userId, String name, String email) {
        DocumentReference userRef = usersCollection.document(userId);
        User userData = new User(name, email);
        userRef.set(userData);
    }

    public void getUser(String userId, final FireStoreDataListener<User> listener) {
        DocumentReference userRef = usersCollection.document(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User userData = document.toObject(User.class);
                        listener.onSuccess(userData);
                    } else {
                        listener.onFailure("User not found");
                    }
                } else {
                    listener.onFailure(task.getException().getMessage());
                }
            }
        });
    }

    private static final String TAG = "FirestoreDataManager";
    private static final String COLLECTION_NAME = "playlists";

    public void getPlaylistsFromFirestore(String userId, OnPlaylistsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<PlaylistData> playlists = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            PlaylistData playlistData = documentSnapshot.toObject(PlaylistData.class);
                            playlists.add(playlistData);
                        }
                        listener.onPlaylistsLoaded(playlists);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error fetching playlists: ", e);
                        listener.onPlaylistsLoadFailed(e.getMessage());
                    }
                });
    }

    public interface OnPlaylistsLoadedListener {
        void onPlaylistsLoaded(List<PlaylistData> playlists);
        void onPlaylistsLoadFailed(String errorMessage);
    }

    public interface FireStoreDataListener<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }

}
