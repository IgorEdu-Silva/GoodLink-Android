package com.example.goodlink.Functions;

import android.content.Context;

import com.example.goodlink.FireBase.PlaylistData;

public class PlaylistDescriptionHelper {
    public static String getDescriptionFromPlaylist(PlaylistData playlist, Context context) {
        if (playlist == null) {
            return "";
        }

        String description = playlist.getDescricao();

        return description != null ? description : "";
    }
}
