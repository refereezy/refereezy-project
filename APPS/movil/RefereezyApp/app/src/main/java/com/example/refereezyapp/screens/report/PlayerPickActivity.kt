package com.example.refereezyapp.screens.report

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.TeamType
import androidx.core.graphics.toColorInt
import com.example.refereezyapp.data.services.SocketService


class PlayerPickActivity : _BaseReportActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player_pick)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // this class extends BaseReportActivity, which initializes the basic values

        // getting previous data
        val type = intent.getSerializableExtra("type") as IncidentType
        val teamSelection = intent.getSerializableExtra("team") as TeamType
        val team = if (teamSelection == TeamType.LOCAL) localTeam else visitorTeam
        val description = intent.getStringExtra("description")

        // components
        val grid = findViewById<GridLayout>(R.id.playerGrid)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)

        for (player in team.players) {
            val inflater = LayoutInflater.from(this).inflate(R.layout.layout_player_pick, grid, false)
            val dorsal = inflater.findViewById<TextView>(R.id.dorsal)

            val color = if (player.goalkeeper) team.secondary_color else team.primary_color

            dorsal.backgroundTintList = ColorStateList.valueOf(color.toColorInt())
            dorsal.text = player.dorsal.toString()

            dorsal.setOnClickListener {
                val incident = Incident(
                    type = type,
                    description = description?: type.name,
                    minute = timer.getElapsedMinutes(),
                    player = player.toPlayerIncident(report.match)
                )

                // this automatically modifies the report and saves into database
                ReportService.addIncident(report, incident)
                SocketService.notifyReportChange(report.raw.id)
                moveTo(ActionActivity::class.java)
                finish()
            }


            grid.addView(inflater)
        }

        flipBtn.setOnClickListener(this::flipScreen)

    }



    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }



}