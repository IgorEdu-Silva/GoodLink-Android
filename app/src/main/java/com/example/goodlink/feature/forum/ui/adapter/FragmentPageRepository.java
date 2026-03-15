package com.example.goodlink.feature.forum.ui.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.core.domain.model.forum.RepositoryItem;
import com.example.goodlink.infrastructure.firebase.database.FireBaseDataBase;
import com.example.goodlink.infrastructure.firebase.firestore.FireStoreDataManager;
import com.example.goodlink.feature.forum.presentation.FilterViewModel;
import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPageRepository extends Fragment {
    private RecyclerView recyclerView;
    private AdapterRepository adapter;
    private List<RepositoryItem> Repository;
    private List<RepositoryItem> repositoryFull;
    private DatabaseReference repositoryRef;
    private FireStoreDataManager firestoreDataManager;
    private Map<String, String> userIdToNameMap;
    private FilterViewModel filterViewModel;
    private SearchView searchView;
    private EditText searchEditText;
    private Button buttonMenuOptionsMain;
    private Map<Integer, Runnable> menuActionMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        menuActionMap = new HashMap<>();
        menuActionMap.put(R.id.orderByDate, this::sortRepositoriesByDate);
        menuActionMap.put(R.id.orderByAlphabetically, this::sortRepositoryAlphabetically);
        menuActionMap.put(R.id.filterByCategory, () -> {
            View anchorView = getActivity().findViewById(R.id.ButtonMenuOptionsMain);
            if (anchorView != null) {
                showCategoryMenu(anchorView);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FireBaseDataBase firebaseDatabase = new FireBaseDataBase();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        firebaseDatabase.testConnection();

        View view = inflater.inflate(R.layout.fragment_page_repository, container, false);
        recyclerView = view.findViewById(R.id.viewRepository);
        Repository = new ArrayList<>();
        firestoreDataManager = new FireStoreDataManager();
        searchView = view.findViewById(R.id.searchRepository);
        buttonMenuOptionsMain = view.findViewById(R.id.ButtonMenuOptionsMain);

        repositoryRef = FirebaseDatabase.getInstance().getReference("repositories");
        adapter = new AdapterRepository(Repository, repositoryRef, getContext(), userIdToNameMap, firestoreDataManager);
        setupRecyclerView();

        firestoreDataManager.getRepositoryFromFirestore(new FireStoreDataManager.OnRepositoryLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRepositoriesLoaded(List<RepositoryItem> repositories) {
                if (userIdToNameMap != null) {
                    Repository.addAll(repositories);
                    adapter.notifyDataSetChanged();
                    repositoryFull = new ArrayList<>(Repository);
                    Toast.makeText(getContext(), "Repositórios carregados com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {

                        @Override
                        public void onUserIdToNameMapLoaded(Map<String, String> map) {

                            userIdToNameMap = map;

                            firestoreDataManager.getRepositoryFromFirestore(new FireStoreDataManager.OnRepositoryLoadedListener() {

                                @Override
                                public void onRepositoriesLoaded(List<RepositoryItem> repositories) {

                                    Repository.clear();
                                    Repository.addAll(repositories);

                                    repositoryFull = new ArrayList<>(repositories);

                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onRepositoriesLoadFailed(String errorMessage) {
                                    Log.e("FragmentPageRepository", errorMessage);
                                }
                            });
                        }

                        @Override
                        public void onUserIdToNameMapLoadFailed(String errorMessage) {
                            Log.e("FragmentPageRepository", errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onRepositoriesLoadFailed(String errorMessage) {
                Log.e("FragmentPageRepository", "Erro ao carregar repositórios do Firestore: " + errorMessage);
            }
        });

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setIconifiedByDefault(false);

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
                    Repository.clear();
                    if (repositoryFull != null) {
                        Repository.addAll(repositoryFull);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Repository.clear();
                    Repository.addAll(filterRepositories(repositoryFull, newFilterText));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        buttonMenuOptionsMain.setOnClickListener(v -> {
            if (!v.isEnabled()) return;

            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            MenuInflater inflaterMenu = popupMenu.getMenuInflater();
            inflaterMenu.inflate(R.menu.menu_options_main, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                Runnable action = menuActionMap.get(item.getItemId());
                if (action != null) {
                    action.run();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        if (currentUser != null && currentUser.isAnonymous()) {
            buttonMenuOptionsMain.setEnabled(false);
            buttonMenuOptionsMain.setAlpha(0.5f);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu: Menu criado");
        inflater.inflate(R.menu.menu_options_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
        Runnable action = menuActionMap.get(item.getItemId());
        if (action != null) {
            action.run();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "Ordenar por Ordem Alfabética");
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Ordenar por Data");

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == 0) {
                sortRepositoryAlphabetically();
                return true;
            } else if (itemId == 1) {
                sortRepositoriesByDate();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortRepositoryAlphabetically() {
        Repository.sort(
                Comparator.comparing(
                        RepositoryItem::title,
                        Comparator.nullsLast(String::compareToIgnoreCase)
                )
        );
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortRepositoriesByDate() {
        Repository.sort(
                Comparator.comparing(
                        this::parseDate,
                        Comparator.nullsLast(LocalDate::compareTo)
                )
        );
        adapter.notifyDataSetChanged();
    }

    private LocalDate parseDate(RepositoryItem item) {
        if (item.publishedAt() == null) return null;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return LocalDate.parse(item.publishedAt(), formatter);
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
                filterRepositoriesByCategory(category);
                return true;
            }
        });

        popupMenu.show();
    }

    public void reloadRepositories() {
        firestoreDataManager.getRepositoryFromFirestore(new FireStoreDataManager.OnRepositoryLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRepositoriesLoaded(List<RepositoryItem> repositories) {
                repositoryFull = new ArrayList<>(repositories);
                Repository.clear();
                Repository.addAll(repositories);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Repositórios recarregadas com sucesso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRepositoriesLoadFailed(String errorMessage) {
                Log.e("FragmentPageRepository", "Erro ao carregar repositórios do Firestore: " + errorMessage);
                Toast.makeText(getContext(), "Erro ao recarregar repositórios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getCategoryList() {
        List<String> categories = new ArrayList<>();
        for (RepositoryItem repository : repositoryFull) {
            String category = repository.category();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterRepositoriesByCategory(String category) {
        List<RepositoryItem> filteredRepositories = new ArrayList<>();
        for (RepositoryItem repository : repositoryFull) {
            if (repository.category().equalsIgnoreCase(category)) {
                filteredRepositories.add(repository);
            }
        }

        Repository.clear();
        Repository.addAll(filteredRepositories);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        if (userIdToNameMap != null) {
            adapter = new AdapterRepository(Repository, repositoryRef, getContext(), userIdToNameMap, firestoreDataManager);
        } else {
            adapter = new AdapterRepository(Repository, repositoryRef, getContext(), new HashMap<>(), firestoreDataManager);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        adapter.setOnItemClickListener(this::openWebPage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private List<RepositoryItem> filterRepositories(List<RepositoryItem> allRepositories, String filterText) {
        if (allRepositories == null) {
            return Collections.emptyList();
        }
        List<RepositoryItem> filteredRepositories = new ArrayList<>();
        for (RepositoryItem repository : allRepositories) {
            String titulo = repository.title();
            String categoria = repository.category();
            String nomeCanal = repository.channelName();

            if (titulo != null && categoria != null && nomeCanal != null) {
                String filter = filterText.toLowerCase();
                if (titulo.toLowerCase().contains(filter) ||
                        categoria.toLowerCase().contains(filter) ||
                        nomeCanal.toLowerCase().contains(filter)) {
                    filteredRepositories.add(repository);
                }
            }
        }
        return filteredRepositories;
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