package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomBarUtil.configurarBotoesBarraInferior(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        Button btnGerenciarUsuarios = findViewById(R.id.btnGerenciarUsuarios);
        btnGerenciarUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(this, GerenciarUsuariosActivity.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}