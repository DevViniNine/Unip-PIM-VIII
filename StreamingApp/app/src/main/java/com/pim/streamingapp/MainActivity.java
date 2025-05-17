package com.pim.streamingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pim.streamingapp.adapters.ConteudoAdapter;
import com.pim.streamingapp.model.Conteudo;
import java.util.*;
import retrofit2.*;

public class MainActivity extends AppCompatActivity {

    LinearLayout feedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        feedContainer = findViewById(R.id.feedContainer);

        carregarConteudos();
    }

    private void carregarConteudos() {
        ApiService api = RetrofitClient.getApiService(MainActivity.this);
        api.getConteudos().enqueue(new Callback<List<Conteudo>>() {
            @Override
            public void onResponse(Call<List<Conteudo>> call, Response<List<Conteudo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    agruparEExibir(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Erro ao carregar conteúdos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Conteudo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agruparEExibir(List<Conteudo> lista) {
        Map<String, List<Conteudo>> mapaPorTipo = new LinkedHashMap<>();

        for (Conteudo c : lista) {
            mapaPorTipo.putIfAbsent(c.tipo, new ArrayList<>());
            mapaPorTipo.get(c.tipo).add(c);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        feedContainer.removeAllViews();

        for (String tipo : Arrays.asList("Video", "Imagem", "Musica", "Podcast")) {
            List<Conteudo> doTipo = mapaPorTipo.get(tipo);
            if (doTipo != null && !doTipo.isEmpty()) {
                TextView header = (TextView) inflater.inflate(R.layout.item_tipo_section, null);
                header.setText(tipo);
                feedContainer.addView(header);

                for (Conteudo conteudo : doTipo) {
                    feedContainer.addView(ConteudoAdapter.gerarItem(this, conteudo));
                }
            }
        }
    }
}
