package com.pim.streamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {
    private TextView txtNomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Configurar barra inferior
        BottomBarUtil.configurarBotoesBarraInferior(this);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuarioPerfil);

        // Buscar nome do usuário logado
        SessionManager session = new SessionManager(this);
        String nomeUsuario = session.getUsuarioNome();
        txtNomeUsuario.setText(nomeUsuario != null ? nomeUsuario : "Nome não encontrado");

        // Botão para editar dados pessoais
        Button btnEditar = findViewById(R.id.btnEditarPerfil);
        btnEditar.setOnClickListener(v -> {
            // TODO: Abrir tela de editar nome/email/senha
            startActivity(new Intent(this, EditarPerfilActivity.class));
        });

        // Botão para cadastro de criador
        Button btnCriador = findViewById(R.id.btnCadastroCriador);
        btnCriador.setOnClickListener(v -> {
            // TODO: Abrir tela/cadastro de criador de conteúdo
            startActivity(new Intent(this, CadastroCriadorActivity.class));
        });

        // Botão para o painel de administrador
        ImageButton btnPerfil = AdminActivity.findViewById(R.id.btnPerfil);
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(AdminActivity, PerfilActivity.class);
                AdminActivity.startActivity(intent);
                AdminActivity.finish();
            });
        }

        // Botão de logout (final da tela, em vermelho)
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
