package com.example.goodlink.core.domain.model.util;

import android.content.Context;

import com.example.goodlink.core.domain.model.ManagerRepository;

public class HelperRepositoryDescription {
    public static String getDescriptionFromRepository(ManagerRepository repository, Context context) {
        if (repository == null) {
            return "";
        }

        String description = repository.getDescricao();

        return description != null ? description : "";
    }
}
