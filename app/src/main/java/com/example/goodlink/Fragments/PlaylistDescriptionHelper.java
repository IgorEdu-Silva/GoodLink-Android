package com.example.goodlink.Fragments;

import android.content.Context;

public class PlaylistDescriptionHelper {
    public static String getDescriptionFromPlaylist(PlaylistData playlist, Context context) {
        if (playlist == null) {
            return "";
        }

        String description = playlist.getDescricao();

        return description != null ? description : "";
    }
}
