package com.example.refereezyapp.screens.report

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.models.TeamType
import com.example.refereezyapp.data.static.ReportManager
import com.example.refereezyapp.screens.fragments.ScoreFragment
import kotlin.collections.forEach

open class _BaseReportActivity : AppCompatActivity() {

    protected lateinit var report: PopulatedReport
    protected var localPoints: Int = 0
    protected var visitorPoints: Int = 0
    protected lateinit var localTeam: Team
    protected lateinit var visitorTeam: Team

    protected lateinit var scoreboard: ScoreFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        report = ReportManager.getCurrentReport()!!

        localTeam = report.match.local_team
        visitorTeam = report.match.visitor_team
        localPoints = countTeamGoals(localTeam.id, report.incidents)
        visitorPoints = countTeamGoals(visitorTeam.id, report.incidents)

        scoreboard = ScoreFragment(localPoints, visitorPoints, localTeam, visitorTeam)

    }


    fun flipScreen(view: View) {
        requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun countTeamGoals(teamId: Int, incidents: List<PopulatedIncident>): Int {
        var goals = 0
        incidents.forEach {
            if (it.player?.team?.id == teamId && it.raw.type == IncidentType.GOAL) goals++
        }

        return goals
    }

    fun moveTo(activity: Class<*>, stack: Boolean = false) {
        val intent = Intent(this, activity)
        if (stack) { intent.putExtras(intent.extras?: Bundle()) }
        startActivity(intent)
    }

    fun moveTo(activity: Class<*>, type: IncidentType, stack: Boolean = false) {
        val intent = Intent(this, activity)
        if (stack) { intent.putExtras(intent.extras?: Bundle()) }
        intent.putExtra("type", type)
        startActivity(intent)
    }

    fun moveTo(activity: Class<*>, team: TeamType) {
        val intent = Intent(this, activity)
        intent.putExtras(intent.extras?: Bundle()) // siempre stackea
        intent.putExtra("team", team)
        startActivity(intent)
    }


}