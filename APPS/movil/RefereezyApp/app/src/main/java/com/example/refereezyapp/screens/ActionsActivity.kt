package com.example.refereezyapp.screens

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.static.ReportManager
import com.example.refereezyapp.screens.fragments.ScoreFragment

class ActionsActivity : AppCompatActivity() {

    private lateinit var report: PopulatedReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        report = ReportManager.getCurrentReport()!!

        val localPoints = countTeamGoals(report.match.local_team.id, report.incidents)
        val visitorPoints = countTeamGoals(report.match.visitor_team.id, report.incidents)
        val localTeam = report.match.local_team
        val visitorTeam = report.match.visitor_team

        // components
        val scoreboard = ScoreFragment(localPoints, visitorPoints, localTeam, visitorTeam)
        val timer = findViewById<TextView>(R.id.timer)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()


        // drawing data
        timer.text = "${report.raw.timer[0]}:${report.raw.timer[1]}"


        flipBtn.setOnClickListener {
            requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

        }




    }


    fun countTeamGoals(teamId: Int, incidents: List<PopulatedIncident>): Int {
        var goals = 0
        incidents.forEach { incident ->
            if (incident.player?.team?.id == teamId &&
                incident.raw.type == IncidentType.GOAL) {
                goals++
            }
        }

        return goals
    }
}