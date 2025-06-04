package com.pim.streamingapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.pim.streamingapp.adapters.CurtidosAdapter;
import com.pim.streamingapp.model.*;
import retrofit2.*;

import java.util.ArrayList;

public class CurtidosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CurtidosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtidos);

        BottomBarUtil.configurarBotoesBarraInferior(this);

        recyclerView = findViewById(R.id.recyclerCurtidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CurtidosAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        ApiService api = RetrofitClient.getApiService(this);
        api.listarCurtidos().enqueue(new Callback<RespostaCurtidosDTO>() {
            @Override
            public void onResponse(Call<RespostaCurtidosDTO> call, Response<RespostaCurtidosDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setLista(response.body().conteudos);
                } else {
                    Toast.makeText(CurtidosActivity.this, "Erro ao carregar curtidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespostaCurtidosDTO> call, Throwable t) {
                Toast.makeText(CurtidosActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
