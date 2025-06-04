package com.pim.streamingapp;

import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.pim.streamingapp.model.UsuarioDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        edtNome = findViewById(R.id.edtNomeEditarPerfil);
        edtEmail = findViewById(R.id.edtEmailEditarPerfil);
        edtSenha = findViewById(R.id.edtSenhaEditarPerfil);
        btnSalvar = findViewById(R.id.btnSalvarPerfil);

        SessionManager session = new SessionManager(this);
        int usuarioId = session.getUsuarioId();
        String nome = session.getUsuarioNome();
        String email = session.getUsuarioEmail();

        edtNome.setText(nome != null ? nome : "");
        edtEmail.setText(email != null ? email : "");
        edtEmail.setInputType(InputType.TYPE_NULL); // Torna não editável
        edtEmail.setEnabled(false);                 // Opcional: bloqueia campo
        edtSenha.setText(""); // Deixe vazio para o usuário preencher se quiser alterar

        btnSalvar.setOnClickListener(v -> {
            String novoNome = edtNome.getText().toString().trim();
            String novaSenha = edtSenha.getText().toString().trim();

            if (novoNome.isEmpty()) {
                Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (novaSenha.length() > 0 && novaSenha.length() < 6) {
                Toast.makeText(this, "Senha deve ter ao menos 6 caracteres!", Toast.LENGTH_SHORT).show();
                return;
            }

            UsuarioDTO dto = new UsuarioDTO();
            dto.id = usuarioId;
            dto.nome = novoNome;
            dto.email = email; // Correto: pega do SessionManager
            dto.password = novaSenha.length() > 0 ? novaSenha : null;
            dto.admin = session.getUsuarioAdmin(); // mantém o valor original

            ApiService api = RetrofitClient.getApiService(this);
            api.alterarUsuario(usuarioId, dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        session.salvarUsuarioNome(novoNome); // Atualiza o nome localmente
                        Toast.makeText(EditarPerfilActivity.this, "Dados alterados!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Erro ao salvar alterações!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditarPerfilActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
