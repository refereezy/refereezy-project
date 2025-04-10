package com.example.refereezyapp.screens.report

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.TeamType
import com.example.refereezyapp.data.static.ReportManager
import net.orandja.shadowlayout.ShadowLayout

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
            val inflater = LayoutInflater.from(this).inflate(R.layout.layout_player_pick, grid, false)
            val dorsal = inflater.findViewById<TextView>(R.id.dorsal)
            val shadow = inflater.findViewById<ShadowLayout>(R.id.shadow)

            val color = if (player.is_goalkeeper) team.secondary_color else team.primary_color
            println("player: $player, color: $color")

            shadow.shadow_color = Color.parseColor(color)
            dorsal.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
            dorsal.text = player.dorsal.toString()


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