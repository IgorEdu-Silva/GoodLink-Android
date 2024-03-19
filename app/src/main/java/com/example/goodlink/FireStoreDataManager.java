package com.example.goodlink;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FireStoreDataManager {
    private FirebaseFirestore firestore;
    private CollectionReference usersCollection;

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

    public interface FireStoreDataListener<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }

}
