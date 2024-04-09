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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private final List<PlaylistData> playlists;
    private static DatabaseReference databaseReference;
    private final Context context;
    private final Map<String, String> userIdToNameMap;


    public PlaylistAdapter(List<PlaylistData> playlists, DatabaseReference databaseReference, Context context, Map<String, String> userIdToNameMap) {
        this.playlists = playlists;
        this.context = context;
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
        PlaylistData playlistData = playlists.get(position);
        holder.bind(playlistData);
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tituloTextView;
        private final TextView descricaoTextView;
        private final TextView nomeCanalTextView;
        private final TextView nomeUsuarioTextView;
        private final TextView dataPubTextView;
        private TextView iframeTextView;
        private TextView urlCanalTextView;
        private TextView categoriaTextView;
        PlaylistData playlistData;

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

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String datePub = sdf.format(new Date());
                dataPubTextView.setText(datePub);
            }
        }

    }
}
