package com.example.goodlink.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.Adapter.AdapterPlaylist;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.FireBaseManager.ManagerPlaylist;
import com.example.goodlink.Functions.FilterViewModel;
import com.example.goodlink.Functions.HelperPlaylistDescription;
import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TabPlaylistsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdapterPlaylist adapter;
    private List<ManagerPlaylist> playlists;
    private List<ManagerPlaylist> playlistsFull;
    private DatabaseReference playlistsRef;
    private FireStoreDataManager firestoreDataManager;
    private Map<String, String> userIdToNameMap;
    private FilterViewModel filterViewModel;
    private SearchView searchView;
    ImageButton btnReloadPlaylists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FireBaseDataBase firebaseDatabase = new FireBaseDataBase();
//        firebaseDatabase.testConnection();

        View view = inflater.inflate(R.layout.fragment_tab_playlists, container, false);
        recyclerView = view.findViewById(R.id.viewPlaylists);
        playlists = new ArrayList<>();
        firestoreDataManager = new FireStoreDataManager();
        searchView = view.findViewById(R.id.searchView);
        ImageButton btnCategoryFilter = view.findViewById(R.id.ButtonFilter);
        btnReloadPlaylists = view.findViewById(R.id.ButtonReloadPlaylists);
        ImageButton btnSortBy = view.findViewById(R.id.ButtonSortBy);
        btnSortBy.setOnClickListener(this::showSortMenu);
        adapter = new AdapterPlaylist(playlists, playlistsRef, getContext(), userIdToNameMap);
        setupRecyclerView();

        firestoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPlaylistsLoaded(List<ManagerPlaylist> loadedPlaylists) {
                if (userIdToNameMap != null) {
                    playlists.addAll(loadedPlaylists);
                    adapter.notifyDataSetChanged();
                    playlistsFull = new ArrayList<>(playlists);
                    Toast.makeText(getContext(), "Playlists carregadas com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
                        @Override
                        public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                            for (ManagerPlaylist playlist : loadedPlaylists) {
                                String fullDescription = HelperPlaylistDescription.getDescriptionFromPlaylist(playlist, getContext());
                                playlist.setDescricao(fullDescription);
                            }

                            setupRecyclerView();
                            playlistsFull = new ArrayList<>(loadedPlaylists);
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
                    if (playlistsFull != null) {
                        playlists.addAll(playlistsFull);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    playlists.clear();
                    playlists.addAll(filterPlaylists(playlistsFull, newFilterText));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btnCategoryFilter.setOnClickListener(this::showCategoryMenu);

        btnReloadPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPlaylists();
            }
        });

        return view;
    }

    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "Ordenar por Ordem Alfabética");
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Ordenar por Data");

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == 0) {
                sortPlaylistsAlphabetically();
                return true;
            } else if (itemId == 1) {
                sortPlaylistsByDate();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortPlaylistsAlphabetically() {
        playlists.sort(new Comparator<ManagerPlaylist>() {
            @Override
            public int compare(ManagerPlaylist playlist1, ManagerPlaylist playlist2) {
                if (playlist1.getTitulo() == null && playlist2.getTitulo() == null) {
                    return 0;
                } else if (playlist1.getTitulo() == null) {
                    return 1;
                } else if (playlist2.getTitulo() == null) {
                    return -1;
                } else {
                    return playlist1.getTitulo().compareToIgnoreCase(playlist2.getTitulo());
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortPlaylistsByDate() {
        playlists.sort(new Comparator<ManagerPlaylist>() {
            @Override
            public int compare(ManagerPlaylist playlist1, ManagerPlaylist playlist2) {
                if (playlist1.getDataPub() == null || playlist2.getDataPub() == null) {
                    return 0;
                }

                String[] date1 = playlist1.getDataPub().split("/");
                String[] date2 = playlist2.getDataPub().split("/");

                int day1 = Integer.parseInt(date1[0]);
                int month1 = Integer.parseInt(date1[1]);
                int year1 = Integer.parseInt(date1[2]);

                int day2 = Integer.parseInt(date2[0]);
                int month2 = Integer.parseInt(date2[1]);
                int year2 = Integer.parseInt(date2[2]);

                if (year1 != year2) {
                    return year1 - year2;
                }

                if (month1 != month2) {
                    return month1 - month2;
                }

                return day1 - day2;
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void showCategoryMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        Menu menu = popupMenu.getMenu();

        List<String> categories = getCategoryList();
        for (String category : categories) {
            menu.add(category);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String category = item.getTitle().toString();
                filterPlaylistsByCategory(category);
                return true;
            }
        });

        popupMenu.show();
    }

    public void reloadPlaylists() {
        firestoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPlaylistsLoaded(List<ManagerPlaylist> loadedPlaylists) {
                playlistsFull = new ArrayList<>(loadedPlaylists);
                playlists.clear();
                playlists.addAll(loadedPlaylists);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Playlists recarregadas com sucesso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaylistsLoadFailed(String errorMessage) {
                Log.e("TabPlaylistsFragment", "Erro ao carregar playlists do Firestore: " + errorMessage);
                Toast.makeText(getContext(), "Erro ao recarregar playlists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getCategoryList() {
        List<String> categories = new ArrayList<>();
        for (ManagerPlaylist playlist : playlistsFull) {
            String category = playlist.getCategoria();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterPlaylistsByCategory(String category) {
        List<ManagerPlaylist> filteredPlaylists = new ArrayList<>();
        for (ManagerPlaylist playlist : playlistsFull) {
            if (playlist.getCategoria().equalsIgnoreCase(category)) {
                filteredPlaylists.add(playlist);
            }
        }

        playlists.clear();
        playlists.addAll(filteredPlaylists);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        adapter = new AdapterPlaylist(playlists, playlistsRef, getContext(), userIdToNameMap);
        adapter.setOnItemClickListener(this::openWebPage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private List<ManagerPlaylist> filterPlaylists(List<ManagerPlaylist> allPlaylists, String filterText) {
        if (allPlaylists == null) {
            return Collections.emptyList();
        }
        List<ManagerPlaylist> filteredPlaylists = new ArrayList<>();
        for (ManagerPlaylist playlist : allPlaylists) {
            String titulo = playlist.getTitulo();
            String categoria = playlist.getCategoria();
            String nomeCanal = playlist.getNomeCanal();

            if (titulo != null && categoria != null && nomeCanal != null) {
                if (titulo.toLowerCase().contains(filterText.toLowerCase()) ||
                        categoria.toLowerCase().contains(filterText.toLowerCase()) ||
                        nomeCanal.toLowerCase().contains(filterText.toLowerCase())) {
                    filteredPlaylists.add(playlist);
                }
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
}