package com.example.goodlink;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseDataBase {
    private static FirebaseFirestore mFirestore;

    private DatabaseReference mDatabase;

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
}
