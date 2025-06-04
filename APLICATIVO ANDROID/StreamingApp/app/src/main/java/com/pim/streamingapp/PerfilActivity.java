package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pim.streamingapp.model.CriadorDTO;
import com.pim.streamingapp.model.UsuarioDTO;

import java.util.List;

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

            String email = session.getUsuarioNome(); // ou get o metodo certo para email
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        Button btnCriador = findViewById(R.id.btnCadastroCriador);
        btnCriador.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(PerfilActivity.this)
                    .setTitle("Cadastro de Criador")
                    .setMessage("Deseja se tornar um criador de conteúdo?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
                            Toast.makeText(PerfilActivity.this, "Nome de usuário não encontrado.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ApiService api = RetrofitClient.getApiService(PerfilActivity.this);

                        // 1. Busca todos os criadores
                        api.listarCriadores().enqueue(new retrofit2.Callback<List<CriadorDTO>>() {
                            @Override
                            public void onResponse(retrofit2.Call<List<CriadorDTO>> call, retrofit2.Response<List<CriadorDTO>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // Verifica se já existe com base no nome (case insensitive)
                                    boolean jaExiste = false;
                                    for (CriadorDTO criador : response.body()) {
                                        if (criador.nome != null && criador.nome.equalsIgnoreCase(nomeUsuario)) {
                                            jaExiste = true;
                                            break;
                                        }
                                    }
                                    if (jaExiste) {
                                        Toast.makeText(PerfilActivity.this, "Você já está cadastrado como criador.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    // 2. Se não existe, cadastra
                                    CriadorDTO dto = new CriadorDTO();
                                    dto.nome = nomeUsuario;

                                    api.cadastrarCriador(dto).enqueue(new retrofit2.Callback<Void>() {
                                        @Override
                                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(PerfilActivity.this, "Cadastro realizado com sucesso! Agora você é um criador de conteúdo.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(PerfilActivity.this, "Erro ao cadastrar como criador.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                                            Toast.makeText(PerfilActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(PerfilActivity.this, "Erro ao consultar criadores!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(retrofit2.Call<List<CriadorDTO>> call, Throwable t) {
                                Toast.makeText(PerfilActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    })
                    .setNegativeButton("Não", null)
                    .show();
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
