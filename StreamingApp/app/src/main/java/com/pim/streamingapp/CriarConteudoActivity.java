package com.pim.streamingapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.pim.streamingapp.model.Conteudo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarConteudoActivity extends AppCompatActivity {
    private EditText edtNome, edtUrl, edtTipo, edtNomeCriador;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conteudo);

        edtNome = findViewById(R.id.edtNomeConteudo);
        edtTipo = findViewById(R.id.edtTipoConteudo);
        edtUrl = findViewById(R.id.edtUrlConteudo);
        edtNomeCriador = findViewById(R.id.edtNomeCriador);
        btnCadastrar = findViewById(R.id.btnCadastrarConteudo);

        // Preencher nome do criador automaticamente se quiser:
        SessionManager session = new SessionManager(this);
        String nomeUsuario = session.getUsuarioNome();
        if (nomeUsuario != null) {
            edtNomeCriador.setText(nomeUsuario);
        }

        btnCadastrar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            String tipo = edtTipo.getText().toString().trim();
            String url = edtUrl.getText().toString().trim();
            String nomeCriador = edtNomeCriador.getText().toString().trim();

            if (nome.isEmpty() || tipo.isEmpty() || url.isEmpty() || nomeCriador.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação extra para o nome do criador
            if (nomeCriador.equalsIgnoreCase("Desconhecido") || nomeCriador.length() < 3) {
                Toast.makeText(this, "Nome do criador inválido. Insira um nome cadastrado ou cadastre um novo criador.", Toast.LENGTH_LONG).show();
                return;
            }

            Conteudo conteudo = new Conteudo();
            conteudo.nome = nome;
            conteudo.tipo = tipo;
            conteudo.url = url;
            conteudo.nomeCriador = nomeCriador;

            ApiService api = RetrofitClient.getApiService(this);
            api.cadastrarConteudo(conteudo).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CriarConteudoActivity.this, "Conteúdo cadastrado!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a tela
                    } else {
                        Toast.makeText(CriarConteudoActivity.this, "Erro ao cadastrar conteúdo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CriarConteudoActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
