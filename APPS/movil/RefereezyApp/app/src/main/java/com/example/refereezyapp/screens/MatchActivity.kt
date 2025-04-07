package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.R
import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.handlers.MatchService
import com.example.refereezyapp.data.handlers.MatchViewModel
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.models.Match
import java.time.format.TextStyle
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.Team
import java.time.format.DateTimeFormatter
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import kotlin.collections.forEach
import com.example.refereezyapp.data.static.ReportManager
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp
import kotlinx.coroutines.runBlocking
import kotlin.collections.isNotEmpty

class MatchActivity : AppCompatActivity() {

    //private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val matchService: MatchViewModel by viewModels()
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
            PopUp.show(this, "Matches found: ${it.size}", PopUp.Type.OK)
            drawMatches()
        }


        matchService.populatedMatch.observe(this) {
            val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, laterMatches, false)
            inflateMatchInfo(it, matchInfo)
        }


        referee = RefereeManager.getCurrentReferee()!!
    }

    override fun onResume() {
        super.onResume()
        matchService.loadMatches(referee.id)
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
        val today = java.time.LocalDate.now()
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
                TextStyle.FULL, java.util.Locale.getDefault())

            val matchDayText = matchInfo.findViewById<TextView>(R.id.matchDayText)
            matchDayText.text = "$dayName $matchDay"
            matchDayText.visibility = View.VISIBLE

            weekMatches.addView(matchInfo)
        }
    }

    fun addMatchGroupTitle(title: String) {
        // Making the section title
        val matchesTitle = TextView(this)
        matchesTitle.id = View.generateViewId()
        matchesTitle.text = title
        styleDayTitle(matchesTitle)
        laterMatches.addView(matchesTitle)
    }

    fun declareMatchButtons(match: Match, matchInfo: View) {

        val clockBtn = matchInfo.findViewById<View>(R.id.clockBtn)
        val whistleBtn = matchInfo.findViewById<View>(R.id.whistleBtn)

        clockBtn.setOnClickListener {
            if (referee.clock_code == null) {
                val intent = Intent(this, PairingClockActivity::class.java)
                startActivity(intent)
                return@setOnClickListener
            }

            prepareReport(match)

        }

        whistleBtn.setOnClickListener {
            prepareReport(match)
        }


    }

    fun prepareReport(match: Match) {
        // Check if there is a current report for the match locally
        var report = ReportManager.getCurrentReport()

        if (report == null || report.raw.done) {
            runBlocking {
                report = FirebaseManager.getReport(referee.id)
            }
        }

        if (report == null || report.raw.done) {
            runBlocking {
                val populatedMatch = MatchService.getMatch(match.id)
                report = reportService.initReport(populatedMatch!!)
            }
        }

        try {

            if (report!!.raw.match_id != match.id) {
                ConfirmationDialog.showReportDialog(
                    this,
                    "Another report already started",
                    "You have to finish the current report before starting a new one",
                    onConfirm = {
                        val intent = Intent(this, ActionsActivity::class.java)
                        startActivity(intent)
                    },
                    onCancel = {
                        // do nothing
                    }

                )
            }
            else {
                val intent = Intent(this, ActionsActivity::class.java)
                startActivity(intent)
            }

        }
        catch (e: Exception) {
            Log.e("MatchActivity", "Error preparing report: ${e.message}")
            PopUp.show(this, "Error preparing report", PopUp.Type.ERROR)
        }

    }

    fun styleDayTitle(title: TextView) {
        title.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = 50.toDp()
            topMargin = 30.toDp()
        }
        title.textSize = 28f
        title.typeface = ResourcesCompat.getFont(this, R.font.k2d_medium)
    }

    fun loadTeamLogo(team: Team, imageView: ImageView) {
        Glide.with(this)
            .load(team.logo_url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
            .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))
    }

    fun Int.toDp(): Int = (this * resources.displayMetrics.density).toInt()

}