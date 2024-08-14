package com.example.goodlink.Functions;

import android.content.Context;

import com.example.goodlink.FireBaseManager.ManagerRepository;

public class HelperRepositoryDescription {
    public static String getDescriptionFromRepository(ManagerRepository repository, Context context) {
        if (repository == null) {
            return "";
        }

        String description = repository.getDescricao();

        return description != null ? description : "";
    }
}
