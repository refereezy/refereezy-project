package com.example.rellotgejais;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private List<Incidencia> incidencias;

    public IncidenciaAdapter(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia incidencia = incidencias.get(position);
        holder.tvTiempo.setText(String.format("%02d:%02d", incidencia.getMins(), incidencia.getSeconds()));
        holder.tvNumero.setText(String.valueOf(incidencia.getJerseyNumber()));
        holder.ivIncidencia.setImageResource(incidencia.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return incidencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTiempo, tvNumero;
        ImageView ivIncidencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            tvNumero = itemView.findViewById(R.id.inccidenciplayerNum);
            ivIncidencia = itemView.findViewById(R.id.incidenciaImage);
        }
    }
}

