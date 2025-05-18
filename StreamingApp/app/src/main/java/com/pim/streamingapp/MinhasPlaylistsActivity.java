package com.pim.streamingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pim.streamingapp.adapters.PlaylistAdapter;
import com.pim.streamingapp.model.Playlist;
import com.pim.streamingapp.session.SessionManager;
import com.pim.streamingapp.api.ApiService;
import com.pim.streamingapp.api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MinhasPlaylistsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnNovaPlaylist;
    private PlaylistAdapter adapter;
    private SessionManager session;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_playlists);

        session = new SessionManager(this);
        api = RetrofitClient.getApiService(this);

        recyclerView = findViewById(R.id.recyclerPlaylists);
        btnNovaPlaylist = findViewById(R.id.btnNovaPlaylist);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnNovaPlaylist.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nome da nova playlist");

            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Criar", (dialog, which) -> {
                String nome = input.getText().toString().trim();
                if (!nome.isEmpty()) {
                    Playlist nova = new Playlist(0, nome);
                    api.criarPlaylist(nova, "Bearer " + session.getToken()).enqueue(new Callback<Playlist>() {
                        @Override
                        public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MinhasPlaylistsActivity.this, "Playlist criada", Toast.LENGTH_SHORT).show();
                                carregarPlaylists(); // Atualiza a lista
                            } else {
                                Toast.makeText(MinhasPlaylistsActivity.this, "Erro ao criar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Playlist> call, Throwable t) {
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
        api.listarPlaylistsDoUsuario("Bearer " + session.getToken()).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PlaylistAdapter(MinhasPlaylistsActivity.this, response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(MinhasPlaylistsActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
