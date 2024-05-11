package com.example.goodlink.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBase.FireStoreDataManager;
import com.example.goodlink.FireBase.PlaylistData;
import com.example.goodlink.R;
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

public class TabFormFragment extends Fragment {
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
    private List<PlaylistData> playlists;
    private PlaylistAdapter playlistAdapter;

    public TabFormFragment(DatabaseReference databaseReference, Map<String, String> userIdToNameMap, RecyclerView recyclerView) {
        this.databaseReference = databaseReference;
        this.userIdToNameMap = userIdToNameMap;
        this.recyclerView = recyclerView;
    }
    public TabFormFragment() {
        this.databaseReference = null;
        this.userIdToNameMap = null;
        this.recyclerView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_form, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tituloEditText = view.findViewById(R.id.tituloPlaylist_Form);
        descricaoEditText = view.findViewById(R.id.descricaoPlaylist_Form);
        nomeCanalEditText = view.findViewById(R.id.nomeCanal_Form);
        iframeEditText = view.findViewById(R.id.iframe_Form);
        urlCanalEditText = view.findViewById(R.id.urlCanal_Form);
        categoriaSpinner = view.findViewById(R.id.categoria_Form);
        enviarButton = view.findViewById(R.id.btnSend_Form);

        playlists = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(playlists, databaseReference, requireContext(), userIdToNameMap);


        loadCategories();

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
                CollectionReference playlistsRef = db.collection("playlists");
                String playlistId = fireStoreDataManager.generatePlaylistId();
                DocumentReference newPlaylistRef = playlistsRef.document(playlistId);

                if (currentUser != null) {
                    String userID = currentUser.getUid();
                    PlaylistData playlistData = new PlaylistData(titulo, descricao, nomeCanal, iframe, urlCanal, categoria, userID, dataPub);
                    PlaylistData playlistDataIDs = new PlaylistData(userID, playlistId);
                    Log.d(TAG, "PlaylistID = " + playlistData.getPlaylistId());

                    fireStoreDataManager.createPlaylist(userID, playlistData, new FireStoreDataManager.OnPlaylistCreatedListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onPlaylistCreated(String playlistId) {
                            playlists.add(playlistData);
                            playlistAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "Playlist criada com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPlaylistCreationFailed(String errorMessage) {
                            Toast.makeText(getActivity(), "Erro ao criar a playlist: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Por favor, faça login para enviar os dados.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private void loadCategories() {
        FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();
        fireStoreDataManager.getPlaylistsFromFirestore(new FireStoreDataManager.OnPlaylistsLoadedListener() {
            @Override
            public void onPlaylistsLoaded(List<PlaylistData> playlists) {
                List<String> categories = new ArrayList<>();
                for (PlaylistData playlist : playlists) {
                    String category = playlist.getCategoria();
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoriaSpinner.setAdapter(adapter);
            }

            @Override
            public void onPlaylistsLoadFailed(String errorMessage) {
                Toast.makeText(getActivity(), "Erro ao carregar categorias: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}