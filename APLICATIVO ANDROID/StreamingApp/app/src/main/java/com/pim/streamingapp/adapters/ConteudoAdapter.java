package com.pim.streamingapp.adapters;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.pim.streamingapp.ExibirConteudoActivity;
import com.pim.streamingapp.R;
import com.pim.streamingapp.model.Conteudo;

public class ConteudoAdapter {

    public static View gerarItem(Context context, Conteudo conteudo) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conteudo, null, false);

        TextView txtNome = view.findViewById(R.id.txtNome);
        TextView txtCriador = view.findViewById(R.id.txtCriador);
        ImageView imgPreview = view.findViewById(R.id.imgPreview);

        txtNome.setText(conteudo.nome);
        txtCriador.setText("Por: " + (conteudo.nomeCriador != null ? conteudo.nomeCriador : "Desconhecido"));


        switch (conteudo.tipo.toLowerCase()) {
            case "video":
                imgPreview.setImageResource(R.drawable.video_player);
                break;
            case "musica":
            case "podcast":
                imgPreview.setImageResource(R.drawable.audio_player);
                break;
            case "imagem":
                imgPreview.setImageResource(R.drawable.image_view);
                break;
            default:
                imgPreview.setImageResource(android.R.drawable.ic_menu_help);
                break;

         }

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExibirConteudoActivity.class);
            Log.d("CONTEUDO_ADAPTER", "ID enviado: " + conteudo.id);
            intent.putExtra("id", conteudo.id);
            intent.putExtra("nome", conteudo.nome);
            intent.putExtra("tipo", conteudo.tipo);
            intent.putExtra("url", conteudo.url);
            intent.putExtra("nomeCriador", conteudo.nomeCriador);
            intent.putExtra("criador", conteudo.criador);
            context.startActivity(intent);

        });

        return view;
    }
}
