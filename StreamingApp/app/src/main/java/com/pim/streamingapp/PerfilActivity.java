package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pim.streamingapp.model.UsuarioDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {
    private TextView txtNomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Button btnAdmin = findViewById(R.id.btnAdmin);

        BottomBarUtil.configurarBotoesBarraInferior(this);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuarioPerfil);

        SessionManager session = new SessionManager(this);
        String nomeUsuario = session.getUsuarioNome();
        txtNomeUsuario.setText(nomeUsuario != null ? nomeUsuario : "Nome não encontrado");

        Button btnEditar = findViewById(R.id.btnEditarPerfil);
        btnEditar.setOnClickListener(v -> {
            startActivity(new Intent(this, EditarPerfilActivity.class));
        });

        Button btnCriador = findViewById(R.id.btnCadastroCriador);
        btnCriador.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroCriadorActivity.class));
        });

        btnAdmin.setOnClickListener(v -> {
            int usuarioId = session.getUsuarioId();
            ApiService api = RetrofitClient.getApiService(this);

            api.getUsuarioPorId(usuarioId).enqueue(new Callback<UsuarioDTO>() {
                @Override
                public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UsuarioDTO usuario = response.body();
                        if (usuario.admin == 1) {
                            startActivity(new Intent(PerfilActivity.this, AdminActivity.class));
                        } else {
                            Toast.makeText(PerfilActivity.this, "Apenas administradores têm acesso!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PerfilActivity.this, "Erro ao buscar informações do usuário!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                    Toast.makeText(PerfilActivity.this, "Erro de conexão!", Toast.LENGTH_SHORT).show();
                }
            });
        });


        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            session.limparSessao();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
