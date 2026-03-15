package com.example.goodlink.feature.forum.presentation;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.goodlink.feature.forum.ui.popup.PopUpReport;

import java.util.Objects;

/** Mantém somente ações de UI que dependem de FragmentManager (não é domínio). */
public final class CommentActionHandler {

    private final FragmentManager fragmentManager;

    public CommentActionHandler(@NonNull FragmentManager fragmentManager) {
        this.fragmentManager = Objects.requireNonNull(fragmentManager);
    }

    public void onReport(@NonNull String repositoryId) {
        PopUpReport.newInstance(repositoryId).show(fragmentManager, "popup_report");
    }
}