package com.example.refereezyapp.screens.report

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.TeamType
import com.example.refereezyapp.data.static.ReportManager
import com.example.refereezyapp.utils.PopUp

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

        report = ReportManager.getCurrentReport()!!

        // getting previous data
        val type = intent.getSerializableExtra("type") as? IncidentType
        val teamSelection = intent.getSerializableExtra("team") as? TeamType
        val team = if (teamSelection == TeamType.LOCAL) localTeam else visitorTeam

        // components
        val timer = findViewById<TextView>(R.id.timer)
        val grid = findViewById<GridLayout>(R.id.playerGrid)

        for (player in team.players) {
            val inflater = LayoutInflater.from(this).inflate(R.layout.layout_player_pick, grid)
            val dorsal = inflater.findViewById<TextView>(R.id.dorsal)
            dorsal.text = player.dorsal.toString()
            dorsal.setTextAppearance(R.style.SpecialButton)

            grid.addView(inflater)
        }


        // drawing data
        // todo: modify the way to access and count time
        timer.text = "${report.raw.timer[0]}:${report.raw.timer[1]}"

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()




    }


    //! this class extends BaseReportActivity, which initializes the basic values





}