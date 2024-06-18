package com.example.goodlink.Functions;

import android.content.Context;

import com.example.goodlink.FireBaseManager.ManagerPlaylist;

public class HelperPlaylistDescription {
    public static String getDescriptionFromPlaylist(ManagerPlaylist playlist, Context context) {
        if (playlist == null) {
            return "";
        }

        String description = playlist.getDescricao();

        return description != null ? description : "";
    }
}
