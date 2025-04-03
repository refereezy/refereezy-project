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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.R
import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.MatchService
import com.example.refereezyapp.data.ReportService
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.Team
import java.time.format.DateTimeFormatter
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import kotlin.collections.forEach
import com.example.refereezyapp.data.static.ReportManager
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp
import kotlin.collections.isNotEmpty

class MatchActivity : AppCompatActivity() {

    //private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val matchService: MatchService by viewModels()
    private val reportService = ReportService
    private lateinit var referee: Referee

    var matches: List<Match> = emptyList()
    lateinit var matchesList: LinearLayout

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
        matchesList = findViewById(R.id.matchesList)

        val profileBtn = findViewById<ImageButton>(R.id.profileBtn)
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        matchService.matches.observe(this) {
            PopUp.show(this, "Matches found: ${it.size}", PopUp.Type.OK)
            drawMatches()
        }


        referee = RefereeManager.getCurrentReferee()!!
        matchService.loadMatches(referee.id)

    }


    fun drawMatches() {

        matches = MatchManager.getMatches()

        // Group the matches by date if past, this week, future
        val groupedMatches: Map<String, List<Match>> = matches.groupBy { match ->
            val matchDate = match.date.toLocalDate()
            val today = java.time.LocalDate.now()
            val oneWeekFromToday = today.plusWeeks(1)

            when {
                matchDate.isBefore(today) -> "Past"
                matchDate.isAfter(oneWeekFromToday) -> "Future" //matches more than a week
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM")
                    matchDate.format(formatter)
                }
            }
        }

        // re-loading data to the linear layout
        matchesList.removeAllViews()


        // this week matches and matches with dates
        val specficDays = groupedMatches.filterKeys { it != "Past" && it != "Future" }

        specficDays.forEach { (date, matches) ->

            if (matches.isNotEmpty()) {
                addMatchGroupTitle(date)

                matches.sortedBy { it.date }.forEach { match ->
                    val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, matchesList, false)

                    inflateMatchInfo(match, matchInfo, false)

                    matchesList.addView(matchInfo)
                }
            }
        }

        // for future weeks
        val futureMatches = groupedMatches["Future"] ?: emptyList()
        if (futureMatches.isNotEmpty()) {
            addMatchGroupTitle("Next matches")

            futureMatches.sortedBy { it.date }.forEach {
                val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, matchesList, false)

                inflateMatchInfo(it, matchInfo, true)

                matchesList.addView(matchInfo)
            }

        }


    }



    fun inflateMatchInfo(match: Match, matchInfo: View, specific: Boolean) {
        val matchDate = match.date.toLocalDate()
        val matchTime = match.date.toLocalTime()
        val matchDay = matchDate.format(DateTimeFormatter.ofPattern("dd/MM"))
        val matchHour = matchTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val timeField = matchInfo.findViewById<TextView>(R.id.matchStartingTime)
        timeField.text = "${if (specific) "$matchDay\n" else ""}$matchHour"

        if (specific) timeField.textSize = 20f

        val localTeam = matchService.getTeam(match.local_team_id)!!
        val visitorTeam = matchService.getTeam(match.visitor_team_id)!!

        val localLogo = matchInfo.findViewById<ImageView>(R.id.localLogo)
        val visitorLogo = matchInfo.findViewById<ImageView>(R.id.visitorLogo)

        loadTeamLogo(localTeam, localLogo)
        loadTeamLogo(visitorTeam, visitorLogo)

        declareMatchButtons(match, matchInfo)
    }

    fun addMatchGroupTitle(title: String) {
        // Making the section title
        val matchesTitle = TextView(this)
        matchesTitle.id = View.generateViewId()
        matchesTitle.text = title
        styleDayTitle(matchesTitle)
        matchesList.addView(matchesTitle)
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
            val resultReport = FirebaseManager.getReport(referee.id)
            // if there is a report ...
            if (resultReport != null) {
                // populates the match of the recent loaded report
                val populatedMatch = matchService.populateMatch(resultReport.match_id!!)
                report = PopulatedReport(resultReport, populatedMatch!!)
            }
            // no pending report was found
            else {
                val populatedMatch = matchService.populateMatch(match.id)
                report = reportService.initReport(populatedMatch!!)
            }
        }

        try {

            if (report.raw.match_id != match.id) {
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