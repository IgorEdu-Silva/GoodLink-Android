package com.example.goodlink.Functions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.goodlink.FireBaseManager.ManagerPlaylist;

import java.util.ArrayList;
import java.util.List;

public class FilterViewModel extends ViewModel {
    private final MutableLiveData<String> filterText = new MutableLiveData<>();
    private final List<ManagerPlaylist> filteredPlaylists = new ArrayList<>();
    private List<ManagerPlaylist> playlists = new ArrayList<>();

    public void setFilterText(String text) {
        filterText.setValue(text);
        filterPlaylists();
    }

    private void filterPlaylists() {
        if (playlists == null) {
            return;
        }
        filteredPlaylists.clear();
        String filterTextLower = filterText.getValue() != null ? filterText.getValue().toLowerCase() : "";
        for (ManagerPlaylist playlist : playlists) {
            if (playlist != null && playlist.getTitulo() != null && playlist.getDescricao() != null) {
                if (playlist.getTitulo().toLowerCase().contains(filterTextLower) ||
                        playlist.getDescricao().toLowerCase().contains(filterTextLower)) {
                    filteredPlaylists.add(playlist);
                }
            }
        }
    }

    public LiveData<String> getFilterText() {
        return filterText;
    }

    public List<ManagerPlaylist> getFilteredPlaylists() {
        return filteredPlaylists;
    }

    public void setPlaylists(List<ManagerPlaylist> playlists) {
        this.playlists = playlists;
    }
}
