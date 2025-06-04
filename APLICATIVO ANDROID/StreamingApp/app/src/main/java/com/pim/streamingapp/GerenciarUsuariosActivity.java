package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pim.streamingapp.model.UsuarioDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GerenciarUsuariosActivity extends AppCompatActivity {
    private ListView listViewUsuarios;
    private ArrayAdapter<String> adapter;
    private List<UsuarioDTO> usuariosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_usuarios);

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        ApiService api = RetrofitClient.getApiService(this);
        api.listarUsuarios().enqueue(new Callback<List<UsuarioDTO>>() {
            @Override
            public void onResponse(Call<List<UsuarioDTO>> call, Response<List<UsuarioDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuariosList = response.body();
                    adapter = new ArrayAdapter<>(GerenciarUsuariosActivity.this,
                            android.R.layout.simple_list_item_1,
                            converterListaParaStrings(usuariosList));
                    listViewUsuarios.setAdapter(adapter);

                    listViewUsuarios.setOnItemClickListener((parent, view, position, id) -> {
                        UsuarioDTO usuarioSelecionado = usuariosList.get(position);
                        mostrarDialogUsuario(usuarioSelecionado);
                    });
                } else {
                    Toast.makeText(GerenciarUsuariosActivity.this, "Erro ao listar usuários.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioDTO>> call, Throwable t) {
                Toast.makeText(GerenciarUsuariosActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> converterListaParaStrings(List<UsuarioDTO> lista) {
        // Exemplo: "3 - Ricardo Alves (admin: não)"
        return new java.util.ArrayList<String>() {{
            for (UsuarioDTO u : lista) {
                add(u.id + " - " + u.nome + (u.admin == 1 ? " (admin)" : ""));
            }
        }};
    }

    private void mostrarDialogUsuario(UsuarioDTO usuario) {
        String[] opcoes = {"Alterar", "Deletar"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Usuário: " + usuario.nome)
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        abrirEditarUsuario(usuario);
                    } else if (which == 1) {
                        deletarUsuario(usuario);
                    }
                })
                .show();
    }

    private void abrirEditarUsuario(UsuarioDTO usuario) {
        Intent intent = new Intent(this, EditarUsuarioAdminActivity.class);
        intent.putExtra("usuario_id", usuario.id);
        intent.putExtra("usuario_nome", usuario.nome);
        intent.putExtra("usuario_email", usuario.email);
        intent.putExtra("usuario_admin", usuario.admin);
        startActivity(intent);
    }

    private void deletarUsuario(UsuarioDTO usuario) {
        Toast.makeText(
                GerenciarUsuariosActivity.this,
                "No momento não é possível deletar usuário vinculado ao banco de dados.",
                Toast.LENGTH_LONG
        ).show();
    }
}

