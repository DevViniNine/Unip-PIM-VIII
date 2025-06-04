package com.pim.streamingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pim.streamingapp.model.UsuarioDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarUsuarioAdminActivity extends AppCompatActivity {
    private EditText edtNome, edtSenha;
    private TextView txtEmail;
    private Switch switchAdmin;
    private Button btnSalvar;

    private int usuarioId;
    private int adminValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario_admin);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        edtNome = findViewById(R.id.edtNomeUsuarioAdmin);
        txtEmail = findViewById(R.id.edtEmailUsuarioAdmin);
        edtSenha = findViewById(R.id.edtSenhaUsuarioAdmin);
        switchAdmin = findViewById(R.id.switchAdmin);
        btnSalvar = findViewById(R.id.btnSalvarUsuarioAdmin);

        usuarioId = getIntent().getIntExtra("usuario_id", -1);
        String nome = getIntent().getStringExtra("usuario_nome");
        String email = getIntent().getStringExtra("usuario_email");
        adminValue = getIntent().getIntExtra("usuario_admin", 0);

        edtNome.setText(nome != null ? nome : "");
        txtEmail.setText(email != null ? email : "");
        switchAdmin.setChecked(adminValue == 1);
        edtSenha.setText("");

        btnSalvar.setOnClickListener(v -> {
            String novoNome = edtNome.getText().toString().trim();
            String novaSenha = edtSenha.getText().toString().trim();
            boolean isAdmin = switchAdmin.isChecked();

            if (novoNome.isEmpty()) {
                Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (novaSenha.isEmpty()) {
                Toast.makeText(this, "Senha obrigatória para salvar alterações!", Toast.LENGTH_LONG).show();
                edtSenha.requestFocus();
                return;
            }
            if (novaSenha.length() < 8) {
                Toast.makeText(this, "A senha deve ter pelo menos 8 caracteres!", Toast.LENGTH_LONG).show();
                edtSenha.requestFocus();
                return;
            }

            UsuarioDTO dto = new UsuarioDTO();
            dto.id = usuarioId;
            dto.nome = novoNome;
            dto.email = email;
            dto.password = novaSenha;
            dto.admin = isAdmin ? 1 : 0;

            Gson gson = new GsonBuilder().serializeNulls().create();
            String debugJson = gson.toJson(dto);
            Log.d("EDITAR_USUARIO_DTO", debugJson);

            ApiService api = RetrofitClient.getApiService(this);
            api.alterarUsuario(usuarioId, dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditarUsuarioAdminActivity.this, "Alterações salvas!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditarUsuarioAdminActivity.this, "Erro ao salvar alterações!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditarUsuarioAdminActivity.this, "Erro de conexão!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
