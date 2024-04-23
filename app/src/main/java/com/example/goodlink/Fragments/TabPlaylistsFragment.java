package com.example.goodlink.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
    private SearchView searchView;
    private static final int refresh_interval = 5000;
    private int lastPlaylistPosition = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FireBaseDataBase firebaseDatabase = new FireBaseDataBase();
//        firebaseDatabase.testConnection();

        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        playlists = new ArrayList<>();
        firestoreDataManager = new FireStoreDataManager();
        searchView = view.findViewById(R.id.searchView);

        if (savedInstanceState != null) {
            lastPlaylistPosition = savedInstanceState.getInt("lastPlaylistPosition", 0);
        }


        firestoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPlaylistsLoaded(List<PlaylistData> playlists) {
                if (userIdToNameMap != null) {
                    TabPlaylistsFragment.this.playlists.addAll(playlists);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Playlists carregadas com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
                        @Override
                        public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                            for (PlaylistData playlist : playlists) {
                                String fullDescription = PlaylistDescriptionHelper.getDescriptionFromPlaylist(playlist, getContext());
                                playlist.setDescricao(fullDescription);
                            }

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterViewModel.setFilterText(newText);
                return true;
            }
        });

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("lastPlaylistPosition", lastPlaylistPosition);

        super.onSaveInstanceState(outState);
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
                        int scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                        playlists.clear();
                        playlists.addAll(loadedPlaylists);
                        playlistsFull = new ArrayList<>(playlists);
                        filterViewModel.setPlaylists(playlists);
                        adapter.notifyDataSetChanged();

                        if (scrollPosition != RecyclerView.NO_POSITION) {
                            recyclerView.getLayoutManager().scrollToPosition(scrollPosition);
                        }
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


    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Não é possível abrir a URL: " + url);
        }
    }

    private void setupRecyclerView() {
        adapter = new PlaylistAdapter(playlists, playlistsRef, getContext(), userIdToNameMap);
        adapter.setOnItemClickListener(this::openWebPage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}