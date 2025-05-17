package com.pim.streamingapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ExibirConteudoActivity extends AppCompatActivity {

    private FrameLayout conteudoContainer;
    private String tipo, url, nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conteudoContainer = findViewById(R.id.conteudoContainer);
        setContentView(R.layout.activity_exibir_conteudo);
        FrameLayout container = findViewById(R.id.conteudoContainer);
        Toast.makeText(this, "Reproduzindo conteúdo...", Toast.LENGTH_SHORT).show();


        ImageButton btnFullscreen = findViewById(R.id.btnFullscreen);
        TextView tituloApp = findViewById(R.id.txtTituloApp);
        LinearLayout interacaoLayout = findViewById(R.id.interacaoLayout);
        EditText edtComentario = findViewById(R.id.edtComentario);
        Button btnComentar = findViewById(R.id.btnComentar);
        TextView txtComentarios = findViewById(R.id.txtComentarios);
        LinearLayout comentariosContainer = findViewById(R.id.comentariosContainer);
        LinearLayout bottomBar = findViewById(R.id.bottomBar);

        // Recebe os dados do conteúdo
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        nome = intent.getStringExtra("nome");
        tipo = intent.getStringExtra("tipo");
        url = intent.getStringExtra("url");

        exibirConteudo();

        if (tipo.equalsIgnoreCase("video") || tipo.equalsIgnoreCase("imagem")) {
            btnFullscreen.setVisibility(View.VISIBLE);
        }

        btnFullscreen.setOnClickListener(v -> {
            boolean emFullscreen = (tituloApp.getVisibility() == View.GONE);

            if (emFullscreen) {
                tituloApp.setVisibility(View.VISIBLE);
                interacaoLayout.setVisibility(View.VISIBLE);
                edtComentario.setVisibility(View.VISIBLE);
                btnComentar.setVisibility(View.VISIBLE);
                txtComentarios.setVisibility(View.VISIBLE);
                comentariosContainer.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                btnFullscreen.setImageResource(android.R.drawable.ic_menu_crop);
            } else {
                tituloApp.setVisibility(View.GONE);
                interacaoLayout.setVisibility(View.GONE);
                edtComentario.setVisibility(View.GONE);
                btnComentar.setVisibility(View.GONE);
                txtComentarios.setVisibility(View.GONE);
                comentariosContainer.setVisibility(View.GONE);
                bottomBar.setVisibility(View.GONE);
                btnFullscreen.setImageResource(android.R.drawable.ic_menu_revert);
            }
        });
    }

    private void exibirConteudo() {
        if (conteudoContainer == null) {
            Toast.makeText(this, "conteudoContainer é NULL", Toast.LENGTH_LONG).show();
        } else {
            conteudoContainer.removeAllViews();
        }


        switch (tipo.toLowerCase()) {
            case "video":
                VideoView videoView = new VideoView(this);
                videoView.setVideoURI(Uri.parse(url));
                videoView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                videoView.setMediaController(new MediaController(this));
                videoView.start();
                conteudoContainer.addView(videoView);
                break;

            case "imagem":
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                new Thread(() -> {
                    try {
                        java.io.InputStream in = new java.net.URL(url).openStream();
                        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(in);
                        runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                conteudoContainer.addView(imageView);
                break;

            case "musica":
            case "podcast":
                TextView txt = new TextView(this);
                txt.setText("Reproduzindo áudio...");
                txt.setPadding(16, 16, 16, 16);
                conteudoContainer.addView(txt);

                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(url);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                TextView erro = new TextView(this);
                erro.setText("Tipo de conteúdo não suportado.");
                conteudoContainer.addView(erro);
                break;
        }

    }
}
