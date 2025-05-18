package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.pim.streamingapp.model.LoginRequest;
import com.pim.streamingapp.model.LoginResponse;
import com.pim.streamingapp.model.UsuarioDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button btnEntrar;
    private TextView txtCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(v -> fazerLogin());
        txtCadastro = findViewById(R.id.txtCadastro);

        txtCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fazerLogin() {
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest login = new LoginRequest();
        login.email = email;
        login.password = senha;

        ApiService api = RetrofitClient.getApiService(LoginActivity.this);
        api.fazerLogin(login).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().token;

                    // Salvar token JWT
                    SessionManager session = new SessionManager(LoginActivity.this);
                    session.salvarToken(token);

                    // Agora buscar o ID do usuário pelo e-mail
                    ApiService apiInterna = RetrofitClient.getApiService(LoginActivity.this);
                    apiInterna.getUsuarioPorEmail(email).enqueue(new Callback<UsuarioDTO>() {
                        @Override
                        public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                session.salvarUsuarioId(response.body().id);


                                session.salvarUsuarioId(response.body().id);
                                session.salvarUsuarioNome(response.body().nome);
                                Toast.makeText(LoginActivity.this,
                                        "Bem-vindo ao projeto de DevViniNine",
                                        Toast.LENGTH_LONG).show();


                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Não foi possível recuperar o ID do usuário.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UsuarioDTO> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Erro ao buscar usuário: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    });

                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}





