package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private final List<PlaylistData> playlists;
    private final AtomicReference<List<PlaylistData>> playlistsFull = new AtomicReference<>();
    private static DatabaseReference databaseReference;
    private final Context context;
    private final Map<String, String> userIdToNameMap;
    private final String filterText = "";


    public PlaylistAdapter(List<PlaylistData> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap) {
        this.playlists = playlists;
        this.context = context;
        this.playlistsFull.set(new ArrayList<>(playlists));
        PlaylistAdapter.databaseReference = databaseReference;
        this.userIdToNameMap = userIdToNameMap;

    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_playlist_fragment, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        if (position >= 0 && position < playlists.size()) {
            holder.bind(playlists.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updatePlaylists(List<PlaylistData> updatedPlaylists) {
        playlists.clear();
        playlists.addAll(updatedPlaylists);
        playlistsFull.get().clear();
        playlistsFull.get().addAll(updatedPlaylists);
        notifyDataSetChanged();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tituloTextView;
        private final TextView descricaoTextView;
        private final TextView nomeCanalTextView;
        private final TextView nomeUsuarioTextView;
        private final TextView dataPubTextView;


        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo_Playlist);
            descricaoTextView = itemView.findViewById(R.id.descricao_Playlist);
            nomeCanalTextView = itemView.findViewById(R.id.nomeCanal_Playlist);
            nomeUsuarioTextView = itemView.findViewById(R.id.nomeUsuario_Playlist);
            dataPubTextView = itemView.findViewById(R.id.dataPub_Playlist);

        }

        public void bind(PlaylistData playlistData) {
            if (playlistData != null) {
                tituloTextView.setText(playlistData.getTitulo());
                descricaoTextView.setText(playlistData.getDescricao());
                nomeCanalTextView.setText(playlistData.getNomeCanal());

                String userId = playlistData.getNomeUsuario();
                String userName = userIdToNameMap.get(userId);

                if (userName != null) {
                    nomeUsuarioTextView.setText(userName);
                } else {
                    nomeUsuarioTextView.setText(userId);
                }

                dataPubTextView.setText(playlistData.getDataPub());
            }
        }

    }
}
