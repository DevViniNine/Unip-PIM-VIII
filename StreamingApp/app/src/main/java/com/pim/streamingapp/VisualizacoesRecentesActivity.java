package com.pim.streamingapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pim.streamingapp.adapters.VisualizacaoAdapter;
import com.pim.streamingapp.model.RespostaVisualizacaoDTO;
import com.pim.streamingapp.model.VisualizacaoDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class VisualizacoesRecentesActivity extends AppCompatActivity {

    private VisualizacaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacoes_recentes);

        // Configura barra inferior
        BottomBarUtil.configurarBotoesBarraInferior(this);

        RecyclerView recycler = findViewById(R.id.recyclerVisualizacoes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VisualizacaoAdapter(this, new ArrayList<>());
        recycler.setAdapter(adapter);

        ApiService api = RetrofitClient.getApiService(this);
        api.listarVisualizacoesRecentes().enqueue(new Callback<RespostaVisualizacaoDTO>() {
            @Override
            public void onResponse(Call<RespostaVisualizacaoDTO> call, Response<RespostaVisualizacaoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VisualizacaoDTO> visualizacoes = response.body().visualizacoes;
                    // Ordenar do mais recente para o mais antigo
                    Collections.sort(visualizacoes, (a, b) -> b.dataVisualizacao.compareTo(a.dataVisualizacao));
                    adapter.setVisualizacoes(visualizacoes);
                    if (visualizacoes.isEmpty()) {
                        Toast.makeText(VisualizacoesRecentesActivity.this, "Nenhum conteúdo visualizado recentemente.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VisualizacoesRecentesActivity.this, "Erro ao carregar visualizações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespostaVisualizacaoDTO> call, Throwable t) {
                Toast.makeText(VisualizacoesRecentesActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
