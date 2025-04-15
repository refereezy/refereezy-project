package com.example.refereezyapp.utils.recyclers

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.models.IncidentType.*
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedReport
import androidx.core.graphics.toColorInt

class IncidentAdapter(
    private val incidents: MutableList<PopulatedIncident>,
    private val report: PopulatedReport
): RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {


    class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val minute: TextView = itemView.findViewById(R.id.minute)
        val dorsal: TextView = itemView.findViewById(R.id.dorsal)
        val typeImage: ImageView = itemView.findViewById(R.id.typeImage)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.layout_incidence_info, parent, false)
        return IncidentViewHolder(inflater)
    }

    override fun onBindViewHolder(
        holder: IncidentViewHolder,
        position: Int
    ) {

        val incident = incidents[position]

        holder.minute.text = "${incident.raw.minute}'"

        if (incident.player != null) {
            val match = report.match
            val team = if (match.isLocalPlayer(incident.player)) match.local_team else match.visitor_team
            val color = if (incident.player.is_goalkeeper) team.secondary_color else team.primary_color
            holder.dorsal.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
            holder.dorsal.text = incident.player.dorsal.toString()
        }
        else holder.dorsal.visibility = View.GONE

        holder.typeImage.setImageResource(when (incident.raw.type) {
            GOAL -> R.drawable.gol
            YELLOW_CARD -> R.drawable.t_amarilla
            RED_CARD -> R.drawable.t_roja
            LESION -> R.drawable.ic_lesion
            FIGHT -> R.drawable.ic_fight
            SUSPEND -> R.drawable.ic_suspend
            OTHER -> R.drawable.ic_warning // not implemented correctly yet
        })

        holder.deleteBtn.setOnClickListener {

            ReportService.removeIncident(report, incident.raw)
            notifyItemRemoved(holder.adapterPosition)
            incidents.removeAt(position)
            notifyItemRangeChanged(position, incidents.size)

        }

    }

    override fun getItemCount(): Int {
        return incidents.size
    }


}