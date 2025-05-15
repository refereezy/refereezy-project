package com.example.refereezyapp.screens.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.MyApp
import com.example.refereezyapp.R
import com.example.refereezyapp.data.services.FirebaseService
import com.example.refereezyapp.data.handlers.MatchService
import com.example.refereezyapp.data.handlers.MatchViewModel
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.handlers.TimerViewModel
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.data.managers.ReportManager
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.services.SocketService
import com.example.refereezyapp.screens.report.ActionActivity
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.net.Socket
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MatchActivity : AppCompatActivity() {

    //private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val matchService: MatchViewModel by viewModels()
    private lateinit var timerViewModel: TimerViewModel
    private val reportService = ReportService
    private lateinit var referee: Referee

    var matches: List<Match> = emptyList()
    lateinit var weekMatches: LinearLayout
    lateinit var laterMatches: LinearLayout
    lateinit var laterMatchesTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_match)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        referee = RefereeManager.getCurrentReferee()!!

        timerViewModel = (application as MyApp).timerViewModel

        // page interactions
        weekMatches = findViewById(R.id.weekMatches)
        laterMatches = findViewById(R.id.laterMatches)
        laterMatchesTitle = findViewById(R.id.laterMatchesTitle)

        val profileBtn = findViewById<ImageButton>(R.id.profileBtn)
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // when matches are loaded
        matchService.matches.observe(this) {
            PopUp.Companion.show(this, "Matches found: ${it.size}", PopUp.Type.OK)
            drawMatches()
        }


        matchService.populatedMatch.observe(this) {
            val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, laterMatches, false)
            inflateMatchInfo(it, matchInfo)
        }

        initSocketListeners()

    }

    override fun onResume() {
        super.onResume()
        matchService.loadMatches(referee.id)
    }

    private fun initSocketListeners() {
        SocketService.socket.on("clock-notified") {
            runOnUiThread {
                PopUp.Companion.show(this, "Clock notified", PopUp.Type.OK)
            }
        }

        SocketService.socket.on("clock-busy") {
            runOnUiThread {
                PopUp.Companion.show(this, "Clock is already in use", PopUp.Type.ERROR)
            }
        }

        SocketService.socket.on("clock-not-online") {
            runOnUiThread {
                PopUp.Companion.show(this, "Clock is not online, try pairing again", PopUp.Type.ERROR)
            }
        }
    }


    fun drawMatches() {

        matches = MatchManager.getMatches()

        // limpiando la pantalla
        laterMatches.removeAllViews()
        laterMatchesTitle.visibility = View.GONE
        weekMatches.removeAllViews()

        matches.forEach {
            // primero se debe cargar el partido por completo (para obtener las imagenes de los equipos)
            matchService.populateMatch(it.id)
        }


    }


    fun inflateMatchInfo(match: PopulatedMatch, matchInfo: View) {
        val matchDate = match.raw.date.toLocalDate()
        val matchTime = match.raw.date.toLocalTime()
        val matchDay = matchDate.format(DateTimeFormatter.ofPattern("dd/MM"))
        val matchHour = matchTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val today = LocalDate.now()
        val oneWeekFromToday = today.plusWeeks(1)
        val specific = matchDate.isAfter(oneWeekFromToday)

        val timeField = matchInfo.findViewById<TextView>(R.id.matchStartingTime)
        val localLogo = matchInfo.findViewById<ImageView>(R.id.localLogo)
        val visitorLogo = matchInfo.findViewById<ImageView>(R.id.visitorLogo)

        val localTeam = match.local_team
        val visitorTeam = match.visitor_team

        loadTeamLogo(localTeam, localLogo)
        loadTeamLogo(visitorTeam, visitorLogo)

        declareMatchButtons(match.raw, matchInfo)

        if (specific) {
            if(!laterMatchesTitle.isVisible) laterMatchesTitle.visibility = View.VISIBLE
            timeField.text = "$matchDay\n$matchHour"
            timeField.textSize = 20f

            laterMatches.addView(matchInfo)
        } else {
            timeField.text = matchHour

            val dayName = matchDate.dayOfWeek.getDisplayName(
                TextStyle.FULL, Locale.ENGLISH)

            val matchDayText = matchInfo.findViewById<TextView>(R.id.matchDayText)
            matchDayText.text = "$dayName $matchDay"
            matchDayText.visibility = View.VISIBLE

            weekMatches.addView(matchInfo)
        }
    }

    fun declareMatchButtons(match: Match, matchInfo: View) {

        val whistleBtn = matchInfo.findViewById<View>(R.id.whistleBtn)
        val clockBtn = matchInfo.findViewById<View>(R.id.clockBtn)

        whistleBtn.setOnClickListener {
            prepareReport(match) { report ->
                // inicia reporte localmente
                ReportManager.setCurrentReport(report)
                goToReport()
            }
        }

        clockBtn.setOnClickListener {
            if (referee.clock_code == null) {
                val intent = Intent(this, PairingClockActivity::class.java)
                startActivity(intent)
            }
            else {
                prepareReport(match){ report ->
                    // notifica a reloj de reporte
                    SocketService.notifyNewReport(report.raw.id, referee.clock_code)
                }

            }
        }


    }

    fun prepareReport(match: Match, toDo: (PopulatedReport) -> Unit) {
        // Check if there is a current report for the match locally
        var report: PopulatedReport? = ReportManager.getCurrentReport()

        runBlocking {
            report = FirebaseService.getReport(referee.id)
        }


        if (report == null || report.raw.done) {
            Log.d("PrepareReport", "Creando nuevo reporte")
            runBlocking {
                val populatedMatch = MatchService.getMatch(match.id)
                report = async { reportService.initReport(populatedMatch!!) }.await()
            }
        }


        try {

            if (report!!.raw.match_id != match.id) {
                Log.d("PrepareReport", "Report ${report.raw.id} already started while asked for ${match.id}")

                ConfirmationDialog.Companion.showReportDialog(
                    this,
                    "Another report already started",
                    "You have to finish the current report before starting a new one",
                    onConfirm = {
                        toDo(report)
                    },
                    onCancel = {
                        // do nothing
                    }

                )
            }
            else {
                toDo(report)
            }

        }
        catch (e: Exception) {
            Log.e("MatchActivity", "Error preparing report", e)
            PopUp.Companion.show(this, "Error preparing report", PopUp.Type.ERROR)
        }

    }

    fun goToReport() {
        val report = ReportManager.getCurrentReport()!!
        timerViewModel.initTimer(report.raw.timer[0], report.raw.timer[1])
        val intent = Intent(this, ActionActivity::class.java)
        startActivity(intent)
    }

    fun loadTeamLogo(team: Team, imageView: ImageView) {
        Glide.with(this)
            .load(team.logo_url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
            .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))
    }


}