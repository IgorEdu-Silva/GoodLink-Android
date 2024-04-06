package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private final List<PlaylistData> playlists;
    private static DatabaseReference databaseReference;
    private Context context;
    private TextView iframeTextView;
    private TextView urlCanalTextView;
    private TextView categoriaTextView;

    public PlaylistAdapter(List<PlaylistData> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_playlist_fragment, parent, false);
        return new PlaylistViewHolder(view, iframeTextView, urlCanalTextView, categoriaTextView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistData playlistData = playlists.get(position);
        holder.bind(playlistData);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final TextView tituloTextView;
        private final TextView descricaoTextView;
        private final TextView nomeCanalTextView;
        private final TextView iframeTextView;
        private final TextView urlCanalTextView;
        private final TextView categoriaTextView;
        private final TextView nomeUsuarioTextView;
        private final TextView dataPubTextView;

        public PlaylistViewHolder(@NonNull View itemView, TextView iframeTextView, TextView urlCanalTextView, TextView categoriaTextView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.titulo_Playlist);
            descricaoTextView = itemView.findViewById(R.id.descricao_Playlist);
            nomeCanalTextView = itemView.findViewById(R.id.nomeCanal_Playlist);
            nomeUsuarioTextView = itemView.findViewById(R.id.nomeUsuario_Playlist);
            dataPubTextView = itemView.findViewById(R.id.dataPub_Playlist);
            this.iframeTextView = iframeTextView;
            this.urlCanalTextView = urlCanalTextView;
            this.categoriaTextView = categoriaTextView;
        }

        public void bind(PlaylistData playlistData) {
            tituloTextView.setText(playlistData.getTitulo());
            descricaoTextView.setText(playlistData.getDescricao());
            nomeCanalTextView.setText(playlistData.getNomeCanal());
            iframeTextView.setText(playlistData.getIframe());
            urlCanalTextView.setText(playlistData.getUrlCanal());
            categoriaTextView.setText(playlistData.getCategoria());
            nomeUsuarioTextView.setText(playlistData.getNomeUsuario());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String datePub = sdf.format(new Date());
            dataPubTextView.setText(datePub);

            setClickableText(urlCanalTextView, playlistData.getUrlCanal(), "urlCanal", databaseReference);
            setClickableText(iframeTextView, playlistData.getIframe(), "iframe", databaseReference);
            setClickableText(categoriaTextView, playlistData.getCategoria(), "categoria", databaseReference);

        }

        private void setClickableText(TextView textView, String categoria, String text, DatabaseReference databaseReference) {
            SpannableString spannableString = new SpannableString(textView.getText());
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    databaseReference.child(textView.getResources().getResourceEntryName(textView.getId())).setValue(text);
                }
            };

            int startIndex = textView.getText().toString().indexOf(text);
            int endIndex = startIndex + text.length();

            spannableString.setSpan(clickableSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
