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
      //  ImageButton btnInicio = findViewById(R.id.btnInicio);
        BottomBarUtil.configurarBotoesBarraInferior(this);



        Button btnMinhasPlaylists = findViewById(R.id.btnMinhasPlaylists);
        btnMinhasPlaylists.setOnClickListener(v -> {
            Intent intent = new Intent(this, MinhasPlaylistsActivity.class);
            startActivity(intent);
        });


        btnVisualizados.setOnClickListener(v -> {
            Intent intent = new Intent(BibliotecaActivity.this, VisualizacoesRecentesActivity.class);
            startActivity(intent);
        });

        btnCurtidos.setOnClickListener(v -> {
            startActivity(new Intent(this, CurtidosActivity.class));
        });

        btnCriar.setOnClickListener(v -> {
            startActivity(new Intent(this, CriarConteudoActivity.class));
        });
    }
}