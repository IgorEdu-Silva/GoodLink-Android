package com.example.goodlink.infrastructure.firebase.user;

import androidx.annotation.NonNull;

import com.example.goodlink.core.domain.model.user.User;
import com.example.goodlink.core.domain.repository.UserRepository;
import com.example.goodlink.infrastructure.firebase.user.dto.UserDto;
import com.example.goodlink.infrastructure.firebase.user.mapper.UserMapper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FirestoreUserRepository implements UserRepository {

    private final CollectionReference users;

    public FirestoreUserRepository(@NonNull FirebaseFirestore db) {
        this.users = Objects.requireNonNull(db).collection("users");
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull Callback<User> cb) {
        users.document(userId).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) {
                        cb.onError("User not found");
                        return;
                    }
                    UserDto dto = snap.toObject(UserDto.class);
                    if (dto == null) {
                        cb.onError("User inválido");
                        return;
                    }
                    cb.onSuccess(UserMapper.toDomain(userId, dto));
                })
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    @Override
    public void getUserIdToNameMap(@NonNull Callback<Map<String, String>> cb) {
        users.get()
                .addOnSuccessListener(result -> {
                    Map<String, String> map = new HashMap<>();
                    result.getDocuments().forEach(doc -> {
                        String name = doc.getString("name");
                        map.put(doc.getId(), name != null ? name : "");
                    });
                    cb.onSuccess(map);
                })
                .addOnFailureListener(e -> cb.onError(msg(e)));
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static String msg(Exception e) { return e.getMessage() == null ? "Erro desconhecido" : e.getMessage(); }
}