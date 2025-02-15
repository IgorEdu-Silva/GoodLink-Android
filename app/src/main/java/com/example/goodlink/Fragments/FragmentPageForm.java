package com.example.goodlink.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.Adapter.AdapterRepository;
import com.example.goodlink.FireBaseManager.FireStoreDataManager;
import com.example.goodlink.FireBaseManager.ManagerRepository;
import com.example.goodlink.R;
import com.example.goodlink.Utils.KeyboardUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FragmentPageForm extends Fragment {
    private EditText tituloEditText;
    private EditText descricaoEditText;
    private EditText nomeCanalEditText;
    private EditText iframeEditText;
    private EditText urlCanalEditText;
    private Spinner categoriaSpinner;
    private Button enviarButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final DatabaseReference databaseReference;
    private final Map<String, String> userIdToNameMap;
    private final RecyclerView recyclerView;
    private List<ManagerRepository> repository;
    private AdapterRepository adapterRepository;

    public FragmentPageForm(DatabaseReference databaseReference, Map<String, String> userIdToNameMap, RecyclerView recyclerView) {
        this.databaseReference = databaseReference;
        this.userIdToNameMap = userIdToNameMap;
        this.recyclerView = recyclerView;
    }
    public FragmentPageForm() {
        this.databaseReference = null;
        this.userIdToNameMap = null;
        this.recyclerView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page_form, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tituloEditText = view.findViewById(R.id.titleOfLink);
        descricaoEditText = view.findViewById(R.id.descriptionContent);
        nomeCanalEditText = view.findViewById(R.id.nameOfCreator);
        iframeEditText = view.findViewById(R.id.urlFromContent);
        urlCanalEditText = view.findViewById(R.id.urlFromCreator);
        categoriaSpinner = view.findViewById(R.id.categories);
        enviarButton = view.findViewById(R.id.btnSend_Form);

        repository = new ArrayList<>();
        adapterRepository = new AdapterRepository(repository, databaseReference, requireContext(), userIdToNameMap);

        loadCategories();

        KeyboardUtils.setKeyboardVisibilityListener(requireActivity(), new KeyboardUtils.KeyboardVisibilityListener() {
            @Override
            public void onKeyboardVisibilityChanged(boolean isVisible, int keyboardHeight) {
                if (isVisible) {
                    scrollIfNeeded(view, R.id.categories);
                } else {
                    resetScroll(view);
                }
            }
        });

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = tituloEditText.getText().toString().trim();
                String descricao = descricaoEditText.getText().toString().trim();
                String nomeCanal = nomeCanalEditText.getText().toString().trim();
                String iframe = iframeEditText.getText().toString().trim();
                String urlCanal = urlCanalEditText.getText().toString().trim();
                String categoria = categoriaSpinner.getSelectedItem().toString().trim();

                if (titulo.isEmpty() || descricao.isEmpty() || nomeCanal.isEmpty() || iframe.isEmpty() || urlCanal.isEmpty() || categoria.isEmpty()) {
                    Toast.makeText(getActivity(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataPub = sdf.format(new Date());

                FirebaseUser currentUser = mAuth.getCurrentUser();
                FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
                CollectionReference repositoriesRef = db.collection("repositories");
                String repositoryId = fireStoreDataManager.generateRepositoryId();
                DocumentReference newRepositoryRef = repositoriesRef.document(repositoryId);

                if (currentUser != null) {
                    String userID = currentUser.getUid();
                    ManagerRepository managerRepository = new ManagerRepository(titulo, descricao, nomeCanal, iframe, urlCanal, categoria, userID, dataPub);
                    ManagerRepository managerRepositoryIDs = new ManagerRepository(userID, repositoryId);
                    Log.d(TAG, "RepositoryID = " + repositoryId);
                    managerRepository.setRepositoryId(repositoryId);

                    fireStoreDataManager.createRepository(userID, managerRepository, new FireStoreDataManager.OnRepositoryCreatedListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onRepositoryCreated(String repositoryId) {
                            repository.add(managerRepository);
                            adapterRepository.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Repositório criada com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRepositoryCreationFailed(String errorMessage) {
                            Toast.makeText(getActivity(), "Erro ao criar a repositório: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Por favor, faça login para enviar os dados.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private void scrollIfNeeded(View view, int categoriaRepositoryForm) {
        View focusedEditText = view.findFocus();
        if (focusedEditText instanceof TextView) {
            int[] location = new int[2];
            focusedEditText.getLocationOnScreen(location);

            Rect rect = new Rect();
            view.getWindowVisibleDisplayFrame(rect);

            int screenHeight = view.getHeight();
            int keyboardHeight = screenHeight - rect.bottom;
            int editTextBottom = location[1] + focusedEditText.getHeight();

            View btnSend = view.findViewById(R.id.btnSend_Form);
            int btnSendHeight = btnSend.getHeight();
            int btnSendBottom = btnSend.getBottom();

            if (keyboardHeight > 0 && btnSendBottom > rect.bottom) {
                int scrollAmount = btnSendBottom - rect.bottom + focusedEditText.getPaddingBottom() + 100;
                view.scrollBy(0, scrollAmount);
            }
        }
    }

    private void resetScroll(View view) {
        view.scrollTo(0, 0);
    }

    private void loadCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Programação");
        categories.add("Matemática");
        categories.add("Português");
        categories.add("Ciência");
        categories.add("Geografia");
        categories.add("História");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

    }
}