package com.pim.streamingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pim.streamingapp.R;
import com.pim.streamingapp.model.VisualizacaoDTO;

import java.text.SimpleDateFormat;
import java.util.*;

public class VisualizacaoAdapter extends RecyclerView.Adapter<VisualizacaoAdapter.ViewHolder> {
    private List<VisualizacaoDTO> visualizacoes;
    private final Context context;

    public VisualizacaoAdapter(Context context, List<VisualizacaoDTO> visualizacoes) {
        this.context = context;
        this.visualizacoes = visualizacoes;
    }

    public void setVisualizacoes(List<VisualizacaoDTO> novaLista) {
        this.visualizacoes = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_visualizacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VisualizacaoDTO v = visualizacoes.get(position);

        // Agora usa os novos campos!
        String nome = (v.nomeConteudo != null && !v.nomeConteudo.isEmpty()) ? v.nomeConteudo : "Sem nome";
        String tipo = (v.tipoConteudo != null && !v.tipoConteudo.isEmpty()) ? v.tipoConteudo : "Desconhecido";
        holder.txtInfo.setText(nome + " (" + tipo + ")");
        holder.txtData.setText(formatarData(v.dataVisualizacao));
    }

    @Override
    public int getItemCount() {
        return visualizacoes != null ? visualizacoes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfo, txtData;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo = itemView.findViewById(R.id.txtInfo);
            txtData = itemView.findViewById(R.id.txtData);
        }
    }

    private String formatarData(String isoDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = parser.parse(isoDate);
            SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            return formatador.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }
}
