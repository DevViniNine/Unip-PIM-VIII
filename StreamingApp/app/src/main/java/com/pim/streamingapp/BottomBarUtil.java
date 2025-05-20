package com.pim.streamingapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

public class BottomBarUtil {
    public static void configurarBotoesBarraInferior(Activity activity) {
        // Botão Início
        ImageButton btnInicio = activity.findViewById(R.id.btnInicio);
        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, "Você já está na tela inicial.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Botão Biblioteca
        ImageButton btnBiblioteca = activity.findViewById(R.id.btnBiblioteca);
        if (btnBiblioteca != null) {
            btnBiblioteca.setOnClickListener(v -> {
                if (!(activity instanceof BibliotecaActivity)) {
                    Intent intent = new Intent(activity, BibliotecaActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(activity, "Você já está na biblioteca.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
