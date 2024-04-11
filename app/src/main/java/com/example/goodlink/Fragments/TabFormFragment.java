package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.goodlink.FireBase.FireStoreDataManager;
import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TabFormFragment extends Fragment {
    private EditText tituloEditText;
    private EditText descricaoEditText;
    private EditText nomeCanalEditText;
    private EditText iframeEditText;
    private EditText urlCanalEditText;
    private EditText categoriaEditText;
    private Button enviarButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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
        categoriaEditText = view.findViewById(R.id.categoria_Form);
        enviarButton = view.findViewById(R.id.btnSend_Form);

        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = tituloEditText.getText().toString();
                String descricao = descricaoEditText.getText().toString();
                String nomeCanal = nomeCanalEditText.getText().toString();
                String iframe = iframeEditText.getText().toString();
                String urlCanal = urlCanalEditText.getText().toString();
                String categoria = categoriaEditText.getText().toString();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataPub = sdf.format(new Date());

                FirebaseUser currentUser = mAuth.getCurrentUser();
                FireStoreDataManager fireStoreDataManager = new FireStoreDataManager();

                if (currentUser != null) {
                    String userID = currentUser.getUid();
                    PlaylistData playlistData = new PlaylistData(titulo, descricao, nomeCanal, iframe, urlCanal, categoria, userID, dataPub);

                    fireStoreDataManager.createPlaylist(userID, playlistData, new FireStoreDataManager.OnPlaylistCreatedListener() {
                        @Override
                        public void onPlaylistCreated(String playlistId) {
                            Toast.makeText(getActivity(), "Playlist criada com sucesso!", Toast.LENGTH_SHORT).show();
                            clearEditTexts();
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

    @Override
    public void onPause() {
        super.onPause();
        clearEditTexts();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearEditTexts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearEditTexts();
    }

    private void clearEditTexts() {
        if (tituloEditText != null) {
            tituloEditText.setText("");
        }
        if (descricaoEditText != null) {
            descricaoEditText.setText("");
        }
        if (nomeCanalEditText != null) {
            nomeCanalEditText.setText("");
        }
        if (iframeEditText != null) {
            iframeEditText.setText("");
        }
        if (urlCanalEditText != null) {
            urlCanalEditText.setText("");
        }
        if (categoriaEditText != null) {
            categoriaEditText.setText("");
        }
    }
}