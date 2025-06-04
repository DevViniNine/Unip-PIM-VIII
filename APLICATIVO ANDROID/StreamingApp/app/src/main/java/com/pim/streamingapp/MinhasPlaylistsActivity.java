package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pim.streamingapp.adapters.PlaylistAdapter;
import com.pim.streamingapp.model.Playlist;
import java.util.List;
import retrofit2.*;

public class MinhasPlaylistsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnNovaPlaylist;
    private PlaylistAdapter adapter;
    private ApiService api;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_playlists);

        recyclerView = findViewById(R.id.recyclerPlaylists);
        btnNovaPlaylist = findViewById(R.id.btnNovaPlaylist);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        session = new SessionManager(this);
        api = RetrofitClient.getApiService(this);


        ImageButton btnInicio = findViewById(R.id.btnInicio);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        btnNovaPlaylist.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nova Playlist");
            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Criar", (dialog, which) -> {
                String nome = input.getText().toString().trim();
                if (!nome.isEmpty()) {
                    Playlist nova = new Playlist(0, nome);
                    api.criarPlaylist(nova).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MinhasPlaylistsActivity.this, "Playlist criada", Toast.LENGTH_SHORT).show();
                                carregarPlaylists();
                            } else {
                                Toast.makeText(MinhasPlaylistsActivity.this, "Erro ao criar playlist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MinhasPlaylistsActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });

        carregarPlaylists();
    }


    private void carregarPlaylists() {
        api.listarPlaylists().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PlaylistAdapter(MinhasPlaylistsActivity.this, response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MinhasPlaylistsActivity.this, "Erro ao carregar playlists", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(MinhasPlaylistsActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
