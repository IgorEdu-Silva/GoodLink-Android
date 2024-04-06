package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.FireBase.FireStoreDataManager;
import com.example.goodlink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class TabPlaylistsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<PlaylistData> playlists;
    private DatabaseReference playlistsRef;
    private FireStoreDataManager firestoreDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        playlists = new ArrayList<>();
        adapter = new PlaylistAdapter(playlists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestoreDataManager = new FireStoreDataManager();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestoreDataManager.getPlaylistsFromFirestore(userId, new FireStoreDataManager.OnPlaylistsLoadedListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onPlaylistsLoaded(List<PlaylistData> playlists) {
                    TabPlaylistsFragment.this.playlists.clear();
                    TabPlaylistsFragment.this.playlists.addAll(playlists);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Playlists carregadas com sucesso", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPlaylistsLoadFailed(String errorMessage) {
                    Log.e("TabPlaylistsFragment", "Erro ao carregar playlists do Firestore: " + errorMessage);
                }
            });
        } else {
            Log.e("TabPlaylistsFragment", "Usuário não está autenticado");
        }
        return view;
    }
}