package com.pim.streamingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pim.streamingapp.R;
import com.pim.streamingapp.model.ConteudoCurtidoDTO;
import com.pim.streamingapp.ExibirConteudoActivity;

import java.util.List;

public class CurtidosAdapter extends RecyclerView.Adapter<CurtidosAdapter.ViewHolder> {
    private List<ConteudoCurtidoDTO> lista;
    private final Context context;

    public CurtidosAdapter(Context context, List<ConteudoCurtidoDTO> lista) {
        this.context = context;
        this.lista = lista;
    }

    public void setLista(List<ConteudoCurtidoDTO> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_visualizacao, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConteudoCurtidoDTO c = lista.get(position);
        holder.txtInfo.setText(c.nome + " (" + c.tipo + ")");
        holder.txtData.setText("Por: " + c.nomeCriador);

        // Clique para exibir o conteúdo
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ExibirConteudoActivity.class);
            intent.putExtra("id", c.conteudoId);
            intent.putExtra("nome", c.nome);
            intent.putExtra("tipo", c.tipo);
            intent.putExtra("url", c.url);
            intent.putExtra("nomeCriador", c.nomeCriador);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfo, txtData;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo = itemView.findViewById(R.id.txtInfo);
            txtData = itemView.findViewById(R.id.txtData);
        }
    }
}
