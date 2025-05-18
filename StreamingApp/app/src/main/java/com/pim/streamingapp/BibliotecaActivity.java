package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BibliotecaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        Button btnPlaylists = findViewById(R.id.btnMinhasPlaylists);
        Button btnVisualizados = findViewById(R.id.btnVisualizados);
        Button btnCurtidos = findViewById(R.id.btnCurtidos);
        Button btnCriar = findViewById(R.id.btnCriarConteudo);
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        btnInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            // Evita criar várias instâncias
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // opcional: fecha a tela atual
        });

        btnPlaylists.setOnClickListener(v -> {
            // TODO: abrir tela de playlists
        });

        btnVisualizados.setOnClickListener(v -> {
            // TODO: abrir visualizados
        });

        btnCurtidos.setOnClickListener(v -> {
            // TODO: abrir curtidos
        });

        btnCriar.setOnClickListener(v -> {
            // TODO: abrir criação de conteúdo
        });
    }
}
