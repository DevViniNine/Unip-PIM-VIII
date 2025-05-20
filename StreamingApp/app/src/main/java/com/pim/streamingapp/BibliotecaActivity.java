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

        Button btnVisualizados = findViewById(R.id.btnVisualizados);
        Button btnCurtidos = findViewById(R.id.btnCurtidos);
        Button btnCriar = findViewById(R.id.btnCriarConteudo);
        ImageButton btnInicio = findViewById(R.id.btnInicio);

        btnInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        ImageButton btnBiblioteca = findViewById(R.id.btnBiblioteca);
        btnBiblioteca.setOnClickListener(v -> {
            Toast.makeText(this, "Você já está na tela Biblioteca.", Toast.LENGTH_SHORT).show();
        });

        Button btnMinhasPlaylists = findViewById(R.id.btnMinhasPlaylists);
        btnMinhasPlaylists.setOnClickListener(v -> {
            Intent intent = new Intent(this, MinhasPlaylistsActivity.class);
            startActivity(intent);
        });

        // CORRETO:
        btnVisualizados.setOnClickListener(v -> {
            Intent intent = new Intent(BibliotecaActivity.this, VisualizacoesRecentesActivity.class);
            startActivity(intent);
        });

        btnCurtidos.setOnClickListener(v -> {
            // TODO: abrir curtidos
        });

        btnCriar.setOnClickListener(v -> {
            // TODO: abrir criação de conteúdo
        });
    }
}