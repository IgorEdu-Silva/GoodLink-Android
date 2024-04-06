package com.example.goodlink.FireBase;

import android.annotation.SuppressLint;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseDataBase {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore mFirestore;
    private final DatabaseReference mDatabase;
    public FireBaseDataBase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }

    public void testConnection() {
        if (mFirestore != null) {
            System.out.println("Conexão com o Firestore bem-sucedida!");
        } else {
            System.out.println("Falha ao conectar ao Firestore!");
        }
    }
    public void checkIfEmailExists(String email, EmailCheckListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = !task.getResult().isEmpty();
                        listener.onEmailExists(emailExists);
                    } else {
                        listener.onEmailExists(false);
                    }
                });
    }

    public interface EmailCheckListener {
        void onEmailExists(boolean exists);
    }
}
