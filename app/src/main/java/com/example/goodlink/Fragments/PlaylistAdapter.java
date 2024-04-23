package com.example.goodlink.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodlink.R;
import com.example.goodlink.Screens.PopUp;
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
    private OnItemClickListener clickListener;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        if (position >= 0 && position < playlists.size()) {
            PlaylistData playlist = playlists.get(position);
            holder.bind(playlists.get(position));
            holder.tituloTextView.setText(playlist.getTitulo());
            holder.nomeCanalTextView.setText(playlist.getNomeCanal());

            String descricao = playlist.getDescricao();
            if (descricao != null) {
                if (descricao.length() > 48) {
                    SpannableString spannableString = getSpannableString(descricao);
                    holder.descricaoTextView.setText(spannableString);
                    holder.descricaoTextView.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    holder.descricaoTextView.setText(descricao);
                    holder.descricaoTextView.setMovementMethod(null);
                }
            } else {
                holder.descricaoTextView.setText("");
                holder.descricaoTextView.setMovementMethod(null);
            }

            holder.nomeCanalTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemClick(playlist.getUrlCanal());
                    }
                }
            });


            holder.tituloTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemClick(playlist.getIframe());
                    }
                }
            });
        }
    }

    @NonNull
    private SpannableString getSpannableString(String descricao) {
        String descricaoResumida = descricao.substring(0, 48) + "... Ver mais";
        SpannableString spannableString = new SpannableString(descricaoResumida);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openPopUp(descricao);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#0078bd"));

            }
        };
        spannableString.setSpan(clickableSpan, descricaoResumida.length() - 8, descricaoResumida.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void openPopUp(String fullDescription) {
        PopUp popUp = PopUp.newInstance(fullDescription);
        popUp.show(((FragmentActivity) context).getSupportFragmentManager(), "pop_up_verMais");
    }

    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
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
