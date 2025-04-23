package com.example.refereezyapp.screens.report

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.refereezyapp.MyApp
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.handlers.TimerViewModel
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.models.TeamType
import com.example.refereezyapp.data.managers.ReportManager
import com.example.refereezyapp.screens.fragments.ScoreFragment
import com.example.refereezyapp.screens.user.MatchActivity
import kotlin.collections.forEach

open class _BaseReportActivity : AppCompatActivity() {

    // data
    protected lateinit var report: PopulatedReport
    protected var localPoints: Int = 0
    protected var visitorPoints: Int = 0
    protected lateinit var localTeam: Team
    protected lateinit var visitorTeam: Team

    // fragments
    protected lateinit var scoreboard: ScoreFragment

    // timer
    protected lateinit var timer: TimerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        report = ReportManager.getCurrentReport()!!
        timer = (application as MyApp).timerViewModel

        localTeam = report.match.local_team
        visitorTeam = report.match.visitor_team

    }

    override fun onResume() {
        super.onResume()
        reloadTeamGoals()

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

    // normal move to, stacks if needed
    fun moveTo(activity: Class<*>, stack: Boolean = false) {
        val intent = Intent(this, activity)
        if (stack) { intent.putExtras(this.intent.extras?: Bundle()) }
        startActivity(intent)
    }

    // move to with description, always stacks
    fun moveTo(activity: Class<*>, description: String?) {
        val intent = Intent(this, activity)
        intent.putExtras(this.intent.extras?: Bundle()) // siempre stackea
        intent.putExtra("description", description)
        startActivity(intent)
    }

    // move to with type, stacks if needed
    fun moveTo(activity: Class<*>, type: IncidentType, stack: Boolean = false) {
        val intent = Intent(this, activity)
        if (stack) { intent.putExtras(this.intent.extras?: Bundle()) }
        intent.putExtra("type", type)
        startActivity(intent)
    }

    // move to with team, stacks if needed
    fun moveTo(activity: Class<*>, team: TeamType) {
        val intent = Intent(this, activity)
        intent.putExtras(this.intent.extras?: Bundle()) // siempre stackea
        intent.putExtra("team", team)
        startActivity(intent)
    }

    // recount goals and sets the fragment again
    private fun reloadTeamGoals() {
        localPoints = countTeamGoals(localTeam.id, report.incidents)
        visitorPoints = countTeamGoals(visitorTeam.id, report.incidents)
        scoreboard = ScoreFragment(localPoints, visitorPoints, localTeam, visitorTeam)
    }

    // ends the report and moves to MatchActivity
    fun endMatchReport() {
        timer.stop()
        ReportService.endReport(report)
        ReportManager.clearReport()
        MatchManager.removeMatch(report.match.raw.id) // esto no hace mucho efecto pues la MatchActivity vuelve a cargarlas
        moveTo(MatchActivity::class.java)
    }


}