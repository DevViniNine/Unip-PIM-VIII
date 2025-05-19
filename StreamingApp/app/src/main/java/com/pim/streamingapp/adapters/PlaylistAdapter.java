package com.pim.streamingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pim.streamingapp.ConteudoPlaylistActivity;
import com.pim.streamingapp.R;
import com.pim.streamingapp.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private Context context;
    private List<Playlist> lista;

    public PlaylistAdapter(Context context, List<Playlist> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = lista.get(position);
        holder.txtNome.setText(playlist.nome);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConteudoPlaylistActivity.class);
            intent.putExtra("playlistId", playlist.id);
            intent.putExtra("playlistNome", playlist.nome);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomePlaylist);
        }
    }
}
