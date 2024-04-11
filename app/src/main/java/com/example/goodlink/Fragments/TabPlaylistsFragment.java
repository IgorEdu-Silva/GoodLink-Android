package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBase.FireBaseDataBase;
import com.example.goodlink.FireBase.FireStoreDataManager;
import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TabPlaylistsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<PlaylistData> playlists;
    private List<PlaylistData> playlistsFull;
    private DatabaseReference playlistsRef;
    private FireStoreDataManager firestoreDataManager;
    private Map<String, String> userIdToNameMap;
    private FilterViewModel filterViewModel;
    private static final int refresh_interval = 5000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FireBaseDataBase firebaseDatabase = new FireBaseDataBase();
//        firebaseDatabase.testConnection();

        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        playlists = new ArrayList<>();
        firestoreDataManager = new FireStoreDataManager();

        firestoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPlaylistsLoaded(List<PlaylistData> playlists) {
                if (userIdToNameMap != null) {
                    TabPlaylistsFragment.this.playlists.clear();
                    TabPlaylistsFragment.this.playlists.addAll(playlists);

                    setupRecyclerView();
                    Toast.makeText(getContext(), "Playlists carregadas com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
                        @Override
                        public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                            setupRecyclerView();
                            Toast.makeText(getContext(), "Playlists carregadas com sucesso", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUserIdToNameMapLoadFailed(String errorMessage) {
                            Log.e("TabPlaylistsFragment", "Erro ao carregar mapa de ID de usuário para nome de usuário: " + errorMessage);
                        }
                    });
                }

            }

            @Override
            public void onPlaylistsLoadFailed(String errorMessage) {
                Log.e("TabPlaylistsFragment", "Erro ao carregar playlists do Firestore: " + errorMessage);
            }
        });

        firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
            @Override
            public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                for (Map.Entry<String, String> entry : userIdToNameMap.entrySet()) {
                    Log.d("UserMap", "UserId: " + entry.getKey() + ", UserName: " + entry.getValue());
                }

                TabPlaylistsFragment.this.userIdToNameMap = userIdToNameMap;
                setupRecyclerView();
            }

            @Override
            public void onUserIdToNameMapLoadFailed(String errorMessage) {
                Log.e("TabPlaylistsFragment", "Erro ao carregar mapa de ID de usuário para nome de usuário: " + errorMessage);
            }
        });

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        filterViewModel.getFilterText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(String newFilterText) {
                if (newFilterText.isEmpty()) {
                    playlists.clear();
                    playlists.addAll(playlistsFull);
                    adapter.notifyDataSetChanged();
                    startAutomaticRefresh();
                } else {
                    playlists.clear();
                    playlists.addAll(filterPlaylists(playlistsFull, newFilterText));
                    adapter.notifyDataSetChanged();
                    stopAutomaticRefresh();
                }
                adapter.notifyDataSetChanged();
            }
        });

        startAutomaticRefresh();

        return view;
    }

    private void startAutomaticRefresh() {
        stopAutomaticRefresh();
        refreshHandler.postDelayed(refreshRunnable, refresh_interval);
    }

    private void stopAutomaticRefresh() {
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    private final Handler refreshHandler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            firestoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onPlaylistsLoaded(List<PlaylistData> loadedPlaylists) {
                    playlists.clear();
                    playlists.addAll(loadedPlaylists);
                    playlistsFull = new ArrayList<>(loadedPlaylists);
                    filterViewModel.setPlaylists(playlists);
                    setupRecyclerView();
                }

                @Override
                public void onPlaylistsLoadFailed(String errorMessage) {
                    Log.e("TabPlaylistsFragment", "Erro ao carregar playlists" + errorMessage);
                }
            });

            startAutomaticRefresh();
        }
    };

    private List<PlaylistData> filterPlaylists(List<PlaylistData> allPlaylists, String filterText) {
        if (allPlaylists == null) {
            return Collections.emptyList();
        }
        List<PlaylistData> filteredPlaylists = new ArrayList<>();
        for (PlaylistData playlist : allPlaylists) {
            if (playlist.getTitulo().toLowerCase().contains(filterText.toLowerCase()) ||
                    playlist.getCategoria().toLowerCase().contains(filterText.toLowerCase()) ||
                    playlist.getNomeCanal().toLowerCase().contains(filterText.toLowerCase())) {
                filteredPlaylists.add(playlist);
            }
        }
        return filteredPlaylists;
    }

    private void setupRecyclerView() {
        adapter = new PlaylistAdapter(playlists, playlistsRef, getContext(), userIdToNameMap);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}