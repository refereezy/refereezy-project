package com.example.rellotgejais.screens

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.rellotgejais.models.PopulatedIncident

class IncidenciaAdapter/*(private val incidencias: List<PopulatedIncident>) */: AppCompatActivity() {
  /*
    RecyclerView.Adapter<IncidenciaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incidencia = incidencias[position]
        holder.tvTiempo.text = String.format("%02d\'", incidencia.raw.mins)
        holder.tvNumero.text = incidencia.player?.dorsal?.toString() ?: "-"
        holder.ivIncidencia.setImageResource(incidencia.imagenResId)
    }

    override fun getItemCount(): Int {
        return incidencias.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTiempo: TextView = itemView.findViewById(R.id.tvTiempo)
        var tvNumero: TextView = itemView.findViewById<TextView>(R.id.dorsal)
        var ivIncidencia: ImageView = itemView.findViewById(R.id.incidenciaImage)
    }


}


class IncidenciaAdapter(private val incidencias: List<PopulatedIncident>) :
    RecyclerView.Adapter<IncidenciaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.item_incidencia, parent, false)
        // return ViewHolder(view)
        return ViewHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // val incidencia = incidencias[position]
        // holder.tvTiempo.text = String.format("%02d\'", incidencia.raw.mins)
        // holder.tvNumero.text = incidencia.player?.dorsal?.toString() ?: "-"
        // holder.ivIncidencia.setImageResource(incidencia.imagenResId)
    }

    override fun getItemCount(): Int {
        // return incidencias.size
        return 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var tvTiempo: TextView = itemView.findViewById(R.id.tvTiempo)
        // var tvNumero: TextView = itemView.findViewById(R.id.dorsal)
        // var ivIncidencia: ImageView = itemView.findViewById(R.id.incidenciaImage)
    }
    */
}