package com.pim.streamingapp;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.pim.streamingapp.model.Conteudo;
import com.pim.streamingapp.adapters.ConteudoAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConteudoPlaylistActivity extends AppCompatActivity {

    private int playlistId;
    private LinearLayout layoutConteudos;
    private TextView txtTituloPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudo_playlist);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        playlistId = getIntent().getIntExtra("playlistId", -1);
        String nomePlaylist = getIntent().getStringExtra("playlistNome");

        layoutConteudos = findViewById(R.id.layoutConteudos);
        txtTituloPlaylist = findViewById(R.id.txtTituloPlaylist);

        if (nomePlaylist != null) {
            txtTituloPlaylist.setText(nomePlaylist);
        }

        if (playlistId != -1) {
            carregarConteudosDaPlaylist();
        } else {
            Toast.makeText(this, "ID de playlist inválido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void carregarConteudosDaPlaylist() {
        ApiService api = RetrofitClient.getApiService(this);
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        api.listarConteudosDaPlaylist(playlistId).enqueue(new Callback<List<Conteudo>>() {
            @Override
            public void onResponse(Call<List<Conteudo>> call, Response<List<Conteudo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LinearLayout layout = findViewById(R.id.layoutConteudos);
                    layout.removeAllViews();
                    List<Conteudo> lista = response.body();
                    if (lista.isEmpty()) {
                        Toast.makeText(ConteudoPlaylistActivity.this, "Nenhum conteúdo nesta playlist.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (Conteudo conteudo : lista) {
                            View v = ConteudoAdapter.gerarItem(ConteudoPlaylistActivity.this, conteudo);
                            layout.addView(v);
                        }
                    }
                } else {
                    Toast.makeText(ConteudoPlaylistActivity.this, "Erro no response: " + response.code(), Toast.LENGTH_LONG).show();
                    if (response.errorBody() != null) {
                        try {
                            Toast.makeText(ConteudoPlaylistActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Conteudo>> call, Throwable t) {
                Toast.makeText(ConteudoPlaylistActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
