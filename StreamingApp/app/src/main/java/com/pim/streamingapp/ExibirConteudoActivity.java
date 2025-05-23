package com.pim.streamingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.gson.Gson;
import com.pim.streamingapp.model.ComentarioDTO;
import com.pim.streamingapp.model.CurtidaDTO;
import com.pim.streamingapp.model.ItemPlaylistDTO;
import com.pim.streamingapp.model.Playlist;
import com.pim.streamingapp.model.RespostaComentarioDTO;
import com.pim.streamingapp.model.ResumoCurtidaResponse;
import com.pim.streamingapp.model.ResumoVisualizacaoResponse;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExibirConteudoActivity extends AppCompatActivity {

    private PlayerView playerView;
    private MediaPlayer audioPlayer;
    @androidx.media3.common.util.UnstableApi
    private SimpleExoPlayer exoPlayer;
    private FrameLayout conteudoContainer;

    private String formatarTempo(int millis) {
        int minutos = (millis / 1000) / 60;
        int segundos = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    private String tipo, url, nome, nomeCriador;
    private int totalCurtidas = 0;

    @androidx.media3.common.util.UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_conteudo);
        BottomBarUtil.configurarBotoesBarraInferior(this);

        Intent intent = getIntent();
        int conteudoId = intent.getIntExtra("id", 0);
        nome = intent.getStringExtra("nome");
        tipo = intent.getStringExtra("tipo");
        url = intent.getStringExtra("url");
        nomeCriador = intent.getStringExtra("nomeCriador");

        Button btnAdicionarPlaylist = findViewById(R.id.btnPlaylist);


        btnAdicionarPlaylist.setOnClickListener(v -> {
            ApiService api = RetrofitClient.getApiService(this);
            SessionManager session = new SessionManager(this);
            String token = "Bearer " + session.getToken();

            // 1. Buscar playlists do usuário
            api.listarPlaylists().enqueue(new Callback<List<Playlist>>() {
                @Override
                public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        List<Playlist> playlists = response.body();
                        String[] nomes = new String[playlists.size()];
                        for (int i = 0; i < playlists.size(); i++) nomes[i] = playlists.get(i).nome;

                        new AlertDialog.Builder(ExibirConteudoActivity.this)
                                .setTitle("Selecione a playlist")
                                .setItems(nomes, (dialog, which) -> {
                                    int playlistId = playlists.get(which).id;
                                    ItemPlaylistDTO dto = new ItemPlaylistDTO(playlistId, conteudoId);

                                    api.adicionarConteudoNaPlaylist(dto).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(ExibirConteudoActivity.this, "Adicionado à playlist!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ExibirConteudoActivity.this, "Já está na playlist!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(ExibirConteudoActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .show();
                    } else {
                        Toast.makeText(ExibirConteudoActivity.this, "Crie uma playlist antes!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<List<Playlist>> call, Throwable t) {
                    Toast.makeText(ExibirConteudoActivity.this, "Falha ao buscar playlists", Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Bind de componentes
        playerView = findViewById(R.id.playerView);
        conteudoContainer = findViewById(R.id.conteudoContainer);
        Button btnDownload = findViewById(R.id.btnDownload);
        Button btnCurtir = findViewById(R.id.btnCurtir);
        LinearLayout comentariosContainer = findViewById(R.id.comentariosContainer);

        SessionManager session = new SessionManager(this);
        int usuarioId = session.getUsuarioId();

        TextView tituloApp = findViewById(R.id.txtTituloApp);
        LinearLayout interacaoLayout = findViewById(R.id.interacaoLayout);
        Button btnComentar = findViewById(R.id.btnComentar);
        TextView txtComentarios = findViewById(R.id.txtComentarios);
        LinearLayout bottomBar = findViewById(R.id.bottomNav);

        Toast.makeText(this, "Carregando conteúdo...", Toast.LENGTH_SHORT).show();
        ApiService api = RetrofitClient.getApiService(this);

        api.registrarVisualizacao(conteudoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("VISUALIZACAO", "Visualização registrada com sucesso");
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("VISUALIZACAO", "Falha ao registrar visualização: " + t.getMessage());
            }
        });

        EditText edtComentario = findViewById(R.id.edtComentario);
        btnComentar.setOnClickListener(v -> {
            String texto = edtComentario.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(this, "Digite um comentário", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG_COMENTARIO", "conteudoId=" + conteudoId);

                return;
            }

            ComentarioDTO comentario = new ComentarioDTO();
            comentario.usuarioId = session.getUsuarioId();
            comentario.conteudoId = conteudoId;
            comentario.usuarioNome = session.getUsuarioNome();
            comentario.texto = texto;

            Log.d("DEBUG_COMENTARIO_JSON", new Gson().toJson(comentario));

            api.comentar(conteudoId, comentario).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ExibirConteudoActivity.this, "Comentado!", Toast.LENGTH_SHORT).show();
                        edtComentario.setText("");

                        comentariosContainer.removeAllViews();
                        carregarComentarios(api, conteudoId, comentariosContainer);
                    } else {
                        String msg = "Erro ao comentar";
                        if (response.errorBody() != null) {
                            try {
                                msg += ": " + response.errorBody().string();
                            } catch (Exception e) { }
                        }
                        Toast.makeText(ExibirConteudoActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.e("COMENTARIO", "Erro: " + msg + " - Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ExibirConteudoActivity.this, "Erro na requisição", Toast.LENGTH_SHORT).show();
                }
            });
        });

        carregarComentarios(api, conteudoId, comentariosContainer);

        final boolean[] curtiu = {false};

        api.verificarCurtida(usuarioId, conteudoId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    curtiu[0] = response.body();
                    btnCurtir.setText(curtiu[0] ? "Descurtir" : "Curtir");
                } else {
                    btnCurtir.setText("Curtir");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                btnCurtir.setText("Curtir");
                Log.e("API", "Erro ao verificar curtida: " + t.getMessage());
            }
        });

        TextView txtTotalCurtidas = findViewById(R.id.txtTotalCurtidas);

        api.listarCurtidas(conteudoId).enqueue(new Callback<ResumoCurtidaResponse>() {
            @Override
            public void onResponse(Call<ResumoCurtidaResponse> call, Response<ResumoCurtidaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    totalCurtidas = response.body().totalCurtidas;
                    txtTotalCurtidas.setText(totalCurtidas + (totalCurtidas == 1 ? " curtida" : " curtidas"));
                }
            }

            @Override
            public void onFailure(Call<ResumoCurtidaResponse> call, Throwable t) {
                txtTotalCurtidas.setText("0 curtidas");
            }
        });

        TextView txtTotalVisualizacoes = findViewById(R.id.txtTotalVisualizacoes);

        api.getTotalVisualizacoes(conteudoId).enqueue(new Callback<ResumoVisualizacaoResponse>() {
            @Override
            public void onResponse(Call<ResumoVisualizacaoResponse> call, Response<ResumoVisualizacaoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int total = response.body().totalVisualizacoes;
                    txtTotalVisualizacoes.setText(total + (total == 1 ? " visualização" : " visualizações"));
                }
            }

            @Override
            public void onFailure(Call<ResumoVisualizacaoResponse> call, Throwable t) {
                txtTotalVisualizacoes.setText("0 visualizações");
            }
        });

        btnCurtir.setOnClickListener(v -> {
            if (!curtiu[0]) {
                api.curtirConteudo(conteudoId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            curtiu[0] = true;
                            btnCurtir.setText("Descurtir");
                            totalCurtidas++;
                            txtTotalCurtidas.setText(totalCurtidas + (totalCurtidas == 1 ? " curtida" : " curtidas"));
                            Toast.makeText(ExibirConteudoActivity.this, "Conteudo curtido", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExibirConteudoActivity.this, "Erro ao curtir", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ExibirConteudoActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                api.descurtirConteudo(conteudoId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            curtiu[0] = false;
                            btnCurtir.setText("Curtir");
                            if (totalCurtidas > 0) totalCurtidas--;
                            txtTotalCurtidas.setText(totalCurtidas + (totalCurtidas == 1 ? " curtida" : " curtidas"));
                            Toast.makeText(ExibirConteudoActivity.this, "Curtida removida", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ExibirConteudoActivity.this, "Erro ao descurtir", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ExibirConteudoActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDownload.setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) {
                Intent intentDownload = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentDownload);
            } else {
                Toast.makeText(this, "URL inválida para download", Toast.LENGTH_SHORT).show();
            }
        });

        exibirConteudo();
    }

    // Novo método para carregar e exibir comentários, ordenando e mostrando a data
    private void carregarComentarios(ApiService api, int conteudoId, LinearLayout comentariosContainer) {
        api.listarComentarios(conteudoId).enqueue(new Callback<RespostaComentarioDTO>() {
            @Override
            public void onResponse(Call<RespostaComentarioDTO> call, Response<RespostaComentarioDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ComentarioDTO> comentarios = response.body().comentarios;
                    // Ordenar do mais novo para o mais antigo usando o campo dataComentario
                    Collections.sort(comentarios, new Comparator<ComentarioDTO>() {
                        @Override
                        public int compare(ComentarioDTO a, ComentarioDTO b) {
                            Date da = parseIsoDate(a.dataComentario);
                            Date db = parseIsoDate(b.dataComentario);
                            if (da == null && db == null) return 0;
                            if (da == null) return 1;
                            if (db == null) return -1;
                            return db.compareTo(da); // Mais novo primeiro
                        }
                    });

                    comentariosContainer.removeAllViews();
                    for (ComentarioDTO c : comentarios) {
                        TextView txtComentario = new TextView(ExibirConteudoActivity.this);
                        String dataFormatada = formatarDataUtcParaLocal(c.dataComentario);
                        txtComentario.setText(dataFormatada + " - " + c.usuarioNome + ": " + c.texto);
                        txtComentario.setPadding(8, 4, 8, 4);
                        comentariosContainer.addView(txtComentario);
                    }
                }
            }

            @Override
            public void onFailure(Call<RespostaComentarioDTO> call, Throwable t) {
                Toast.makeText(ExibirConteudoActivity.this, "Erro ao carregar comentários", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Função para converter data UTC (ISO) para data local formatada
    private String formatarDataUtcParaLocal(String utcIsoDate) {
        if (utcIsoDate == null) return "";
        try {
            String pattern = utcIsoDate.contains(".") ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : "yyyy-MM-dd'T'HH:mm:ss";
            if (utcIsoDate.endsWith("Z")) utcIsoDate = utcIsoDate.replace("Z", "");
            SimpleDateFormat isoFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(utcIsoDate);
            SimpleDateFormat localFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(date);
        } catch (Exception e) {
            return utcIsoDate;
        }
    }

    // Função para parsear ISO para Date, para ordenação
    private Date parseIsoDate(String isoDate) {
        if (isoDate == null) return null;
        try {
            String pattern = isoDate.contains(".") ? "yyyy-MM-dd'T'HH:mm:ss.SSS" : "yyyy-MM-dd'T'HH:mm:ss";
            if (isoDate.endsWith("Z")) isoDate = isoDate.replace("Z", "");
            SimpleDateFormat isoFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return isoFormat.parse(isoDate);
        } catch (ParseException e) {
            return null;
        }
    }

    @androidx.media3.common.util.UnstableApi
    private void exibirConteudo() {
        if (tipo == null || url == null) {
            Toast.makeText(this, "Conteúdo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (tipo.toLowerCase()) {
            case "video":
                playerView.setVisibility(View.VISIBLE);
                conteudoContainer.setVisibility(View.GONE);

                exoPlayer = new SimpleExoPlayer.Builder(this).build();
                playerView.setPlayer(exoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
                break;

            case "imagem":
                playerView.setVisibility(View.GONE);
                conteudoContainer.setVisibility(View.VISIBLE);
                conteudoContainer.removeAllViews();

                ImageView imageView = new ImageView(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(16, 16, 16, 16);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                new Thread(() -> {
                    try {
                        InputStream in = new URL(url).openStream();
                        android.graphics.Bitmap bitmap = BitmapFactory.decodeStream(in);
                        runOnUiThread(() -> {
                            conteudoContainer.addView(imageView);
                            imageView.setImageBitmap(bitmap);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

            case "musica":
            case "podcast":
                if (audioPlayer != null) {
                    if (audioPlayer.isPlaying()) audioPlayer.stop();
                    audioPlayer.release();
                    audioPlayer = null;
                }

                playerView.setVisibility(View.GONE);
                conteudoContainer.setVisibility(View.VISIBLE);
                conteudoContainer.removeAllViews();

                LinearLayout audioLayout = new LinearLayout(this);
                audioLayout.setOrientation(LinearLayout.VERTICAL);
                audioLayout.setPadding(32, 32, 32, 32);

                TextView txtStatus = new TextView(this);
                txtStatus.setText("Carregando áudio...");
                txtStatus.setTextSize(18);
                audioLayout.addView(txtStatus);

                SeekBar seekBar = new SeekBar(this);
                audioLayout.addView(seekBar);

                TextView txtTempo = new TextView(this);
                txtTempo.setText("00:00 / 00:00");
                audioLayout.addView(txtTempo);

                Button btnPlayPause = new Button(this);
                btnPlayPause.setText("Pause");
                audioLayout.addView(btnPlayPause);

                conteudoContainer.addView(audioLayout);

                Handler handler = new Handler();

                audioPlayer = new MediaPlayer();
                try {
                    audioPlayer.setDataSource(url);

                    Runnable updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (audioPlayer != null && audioPlayer.isPlaying()) {
                                seekBar.setProgress(audioPlayer.getCurrentPosition());
                                txtTempo.setText(formatarTempo(audioPlayer.getCurrentPosition()) + " / " + formatarTempo(audioPlayer.getDuration()));
                                handler.postDelayed(this, 500);
                            }
                        }
                    };

                    audioPlayer.setOnPreparedListener(mp -> {
                        txtStatus.setText("Reproduzindo áudio...");
                        audioPlayer.start();
                        seekBar.setMax(audioPlayer.getDuration());
                        handler.post(updateSeekBar);
                    });

                    audioPlayer.prepareAsync();

                    btnPlayPause.setOnClickListener(v -> {
                        if (audioPlayer.isPlaying()) {
                            audioPlayer.pause();
                            btnPlayPause.setText("Play");
                        } else {
                            audioPlayer.start();
                            btnPlayPause.setText("Pause");
                        }
                    });

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) audioPlayer.seekTo(progress);
                        }
                        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    txtStatus.setText("Erro ao carregar áudio.");
                }
                break;

            default:
                playerView.setVisibility(View.GONE);
                conteudoContainer.setVisibility(View.VISIBLE);
                conteudoContainer.removeAllViews();

                TextView erro = new TextView(this);
                erro.setText("Tipo de conteúdo não suportado.");
                conteudoContainer.addView(erro);
                break;
        }
    }

    @Override
    @androidx.media3.common.util.UnstableApi
    protected void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }

        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }

        super.onDestroy();
    }
}
