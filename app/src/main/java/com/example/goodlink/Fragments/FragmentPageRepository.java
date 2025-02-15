package com.example.goodlink.Fragments;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.Adapter.AdapterRepository;
import com.example.goodlink.FireBaseManager.FireBaseDataBase;
import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.FireBaseManager.ManagerRepository;
import com.example.goodlink.ViewModels.FilterViewModel;
import com.example.goodlink.Functions.HelperRepositoryDescription;
import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPageRepository extends Fragment {
    private RecyclerView recyclerView;
    private AdapterRepository adapter;
    private List<ManagerRepository> Repository;
    private List<ManagerRepository> repositoryFull;
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
//        firebaseDatabase.testConnection();

        View view = inflater.inflate(R.layout.fragment_page_repository, container, false);
        recyclerView = view.findViewById(R.id.viewRepository);
        Repository = new ArrayList<>();
        firestoreDataManager = new FireStoreDataManager();
        searchView = view.findViewById(R.id.searchRepository);
        buttonMenuOptionsMain = view.findViewById(R.id.ButtonMenuOptionsMain);

        adapter = new AdapterRepository(Repository, repositoryRef, getContext(), userIdToNameMap);
        setupRecyclerView();

        firestoreDataManager.getRepositoryFromFirestore(new FireStoreDataManager.OnRepositoryLoadedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRepositoriesLoaded(List<ManagerRepository> repositories) {
                if (userIdToNameMap != null) {
                    Repository.addAll(repositories);
                    adapter.notifyDataSetChanged();
                    repositoryFull = new ArrayList<>(Repository);
                    Toast.makeText(getContext(), "Repositórios carregados com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
                        @Override
                        public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                            for (ManagerRepository repository : repositories) {
                                String fullDescription = HelperRepositoryDescription.getDescriptionFromRepository(repository, getContext());
                                repository.setDescricao(fullDescription);
                            }

                            setupRecyclerView();
                            repositoryFull = new ArrayList<>(repositories);
                            if (isAdded()) {
                                try {
                                    Toast.makeText(requireContext(), "Repositórios carregados com sucesso", Toast.LENGTH_SHORT).show();
                                } catch (IllegalStateException e) {
                                    Log.e("FragmentPageRepository", "Contexto não disponível", e);
                                }
                            }
                        }

                        @Override
                        public void onUserIdToNameMapLoadFailed(String errorMessage) {
                            Log.e("FragmentPageRepository", "Erro ao carregar mapa de ID de usuário para nome de usuário: " + errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onRepositoriesLoadFailed(String errorMessage) {
                Log.e("FragmentPageRepository", "Erro ao carregar repositórios do Firestore: " + errorMessage);
            }
        });

        firestoreDataManager.getUserIdToNameMap(new FireStoreDataManager.OnUserIdToNameMapListener() {
            @Override
            public void onUserIdToNameMapLoaded(Map<String, String> userIdToNameMap) {
                FragmentPageRepository.this.userIdToNameMap = userIdToNameMap;
                setupRecyclerView();
            }

            @Override
            public void onUserIdToNameMapLoadFailed(String errorMessage) {
                Log.e("FragmentPageRepository", "Erro ao carregar mapa de ID de usuário para nome de usuário: " + errorMessage);
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


        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreDataManager.getRepositoryFromFirestore(new FireStoreDataManager.OnRepositoryLoadedListener(){
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRepositoriesLoaded(List<ManagerRepository> repositories){
                Repository.clear();
                Repository.addAll(repositories);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRepositoriesLoadFailed(String errorMessage) {
                Log.e("FragmentPageRepository", "Erro ao carregar repositórios: " + errorMessage);
            }
        });
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
        Repository.sort(new Comparator<ManagerRepository>() {
            @Override
            public int compare(ManagerRepository repository, ManagerRepository repository1) {
                if (repository.getTitulo() == null && repository1.getTitulo() == null) {
                    return 0;
                } else if (repository.getTitulo() == null) {
                    return 1;
                } else if (repository1.getTitulo() == null) {
                    return -1;
                } else {
                    return repository.getTitulo().compareToIgnoreCase(repository1.getTitulo());
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortRepositoriesByDate() {
        Repository.sort(new Comparator<ManagerRepository>() {
            @Override
            public int compare(ManagerRepository repository, ManagerRepository repository1) {
                if (repository.getDataPub() == null || repository1.getDataPub() == null) {
                    return 0;
                }

                String[] date1 = repository.getDataPub().split("/");
                String[] date2 = repository1.getDataPub().split("/");

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
            public void onRepositoriesLoaded(List<ManagerRepository> repositories) {
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
        for (ManagerRepository repository : repositoryFull) {
            String category = repository.getCategoria();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterRepositoriesByCategory(String category) {
        List<ManagerRepository> filteredRepositories = new ArrayList<>();
        for (ManagerRepository repository : repositoryFull) {
            if (repository.getCategoria().equalsIgnoreCase(category)) {
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
            adapter = new AdapterRepository(Repository, repositoryRef, getContext(), userIdToNameMap);
        } else {
            adapter = new AdapterRepository(Repository, repositoryRef, getContext(), new HashMap<>());
        }
        adapter.setOnItemClickListener(this::openWebPage);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }

    private List<ManagerRepository> filterRepositories(List<ManagerRepository> allRepositories, String filterText) {
        if (allRepositories == null) {
            return Collections.emptyList();
        }
        List<ManagerRepository> filteredRepositories = new ArrayList<>();
        for (ManagerRepository repository : allRepositories) {
            String titulo = repository.getTitulo();
            String categoria = repository.getCategoria();
            String nomeCanal = repository.getNomeCanal();

            if (titulo != null && categoria != null && nomeCanal != null) {
                if (titulo.toLowerCase().contains(filterText.toLowerCase()) ||
                        categoria.toLowerCase().contains(filterText.toLowerCase()) ||
                        nomeCanal.toLowerCase().contains(filterText.toLowerCase())) {
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