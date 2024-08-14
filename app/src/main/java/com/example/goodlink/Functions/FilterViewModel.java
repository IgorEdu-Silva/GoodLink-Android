package com.example.goodlink.Functions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.goodlink.FireBaseManager.ManagerRepository;

import java.util.ArrayList;
import java.util.List;

public class FilterViewModel extends ViewModel {
    private final MutableLiveData<String> filterText = new MutableLiveData<>();
    private final List<ManagerRepository> filteredRepository = new ArrayList<>();
    private List<ManagerRepository> repositories = new ArrayList<>();

    public void setFilterText(String text) {
        filterText.setValue(text);
        filterRepositories();
    }

    private void filterRepositories() {
        if (repositories == null) {
            return;
        }
        filteredRepository.clear();
        String filterTextLower = filterText.getValue() != null ? filterText.getValue().toLowerCase() : "";
        for (ManagerRepository repository : repositories) {
            if (repository != null && repository.getTitulo() != null && repository.getDescricao() != null) {
                if (repository.getTitulo().toLowerCase().contains(filterTextLower) ||
                        repository.getDescricao().toLowerCase().contains(filterTextLower)) {
                    filteredRepository.add(repository);
                }
            }
        }
    }

    public LiveData<String> getFilterText() {
        return filterText;
    }

    public List<ManagerRepository> getFilteredRepository() {
        return filteredRepository;
    }

    public void setRepositories(List<ManagerRepository> repositories) {
        this.repositories = repositories;
    }
}
