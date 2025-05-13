package com.example.rellotgejais.screens.report

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.handlers.SocketHandler
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.models.IncidentType
import com.example.rellotgejais.models.TeamType

class PlayerPickActivity : _BaseReportActivity() {

    private val socketHandler: SocketHandler by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_player_pick)

        // this class extends BaseReportActivity, which initializes the basic values

        // getting previous data
        val type = intent.getSerializableExtra("type") as IncidentType
        val teamSelection = intent.getSerializableExtra("team") as TeamType
        val team = if (teamSelection == TeamType.LOCAL) localTeam else visitorTeam
        val description = intent.getStringExtra("description")

        // PopUp.show(this, "$type for ...", PopUp.Type.INFO)

        // components
        val grid = findViewById<GridLayout>(R.id.playerGrid)
        for (player in team.players) {
            val inflater = LayoutInflater.from(this).inflate(R.layout.layout_player_pick, grid, false)
            val dorsal = inflater.findViewById<TextView>(R.id.dorsal)
            val baseColor = inflater.findViewById<LinearLayout>(R.id.baseColor)

            val color = if (player.goalkeeper) team.secondary_color else team.primary_color

            baseColor.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
            dorsal.text = player.dorsal.toString()

            inflater.setOnClickListener {
                val incident = Incident(
                    type = type,
                    description = description?: type.name,
                    minute = timer.getElapsedMinutes(),
                    player = player.toPlayerIncident(report.match)
                )

                // notifica al socket del cambio
                socketHandler.notifyNewIncident(report.raw.id, incident)

                // this automatically modifies the report and saves into database
                ReportHandler.addIncident(report, incident)
                moveTo(ActionsActivity::class.java)
                finish()
            }


            grid.addView(inflater)
        }


    }



    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }

}