package com.example.rellotgejais.screens.report

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.handlers.SocketHandler
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.models.IncidentType
import com.example.rellotgejais.models.PopulatedIncident
import com.example.rellotgejais.models.PopulatedReport
import com.example.rellotgejais.models.Team
import com.example.rellotgejais.models.TeamType
import com.example.rellotgejais.models.TimerViewModel
import com.example.rellotgejais.screens.fragments.ScoreFragment

open class _BaseReportActivity : AppCompatActivity() {

    protected val socketHandler: SocketHandler by viewModels()
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

        reloadTeamGoals()

    }

    override fun onResume() {
        super.onResume()
        println(scoreboard.toString())
        reloadTeamGoals()

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
        val intent = Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (stack) {
            intent.putExtras(this.intent.extras ?: Bundle())
        }
        startActivity(intent)
    }

    // move to with description, always stacks
    fun moveTo(activity: Class<*>, description: String?) {
        val intent = Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        intent.putExtras(this.intent.extras ?: Bundle()) // siempre stackea
        intent.putExtra("description", description)
        startActivity(intent)
    }

    // move to with type, stacks if needed
    fun moveTo(activity: Class<*>, type: IncidentType, stack: Boolean = false) {
        val intent = Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (stack) {
            intent.putExtras(this.intent.extras ?: Bundle())
        }
        intent.putExtra("type", type)
        startActivity(intent)
    }

    // move to with team, stacks if needed
    fun moveTo(activity: Class<*>, team: TeamType) {
        val intent = Intent(this, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        intent.putExtras(this.intent.extras ?: Bundle()) // siempre stackea
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
        ReportHandler.endReport(report)
        ReportManager.clearReport()
        val qr = LocalStorageService.getClockQrCode()!!
        socketHandler.unlinkReport(qr, report.raw.id)
        timer.resetTimer()
        finish()
    }
}