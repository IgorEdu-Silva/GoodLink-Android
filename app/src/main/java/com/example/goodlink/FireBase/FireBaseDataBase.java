package com.example.goodlink.FireBase;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseDataBase {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore mFirestore;
    private final DatabaseReference mDatabase;
    private static FirebaseDatabase mFirebaseDatabase;

    public FireBaseDataBase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void testConnection() {
        if (mFirebaseDatabase != null) {
            Log.d("FireBaseDataBase", "Conexão com o Firebase bem-sucedida!");
        } else {
            Log.e("FireBaseDataBase", "Falha ao conectar ao Firebase!");
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
