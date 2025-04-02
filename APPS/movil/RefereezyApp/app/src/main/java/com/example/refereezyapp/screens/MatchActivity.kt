package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
import com.example.refereezyapp.data.MatchService
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.Team
import java.time.format.DateTimeFormatter
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty

class MatchActivity : AppCompatActivity() {

    //private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val matchService: MatchService by viewModels()

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
            drawMatches()
        }



        val referee = RefereeManager.getCurrentReferee()!!
        matchService.loadMatches(referee.id)



        // then each match in this week are loaded using the layout @layout/layout_match_info

        // then the matches more than a week from now are shown




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

        // first the past matches are shown using a TextView with the style="@style/MatchDayTitle"
        val pastMatches = groupedMatches["Past"] ?: emptyList()
        if (pastMatches.isNotEmpty()) {
            val sortedPastMatches = pastMatches.sortedBy { it.date }

            addMatchGroupTitle("Past Month")

            sortedPastMatches.forEach { match ->
                val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, matchesList, false)

                inflateMatchInfo(match, matchInfo, true)

                matchesList.addView(matchInfo)
            }
        }

        // this week matches and matches with dates
        val specficDays = groupedMatches.filterKeys { it != "Past" && it != "Future" }

        specficDays.forEach { (date, matches) ->

            if (matches.isNotEmpty()) {
                addMatchGroupTitle(date)

                matches.forEach { match ->
                    val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, matchesList, false)

                    inflateMatchInfo(match, matchInfo, false)

                    matchesList.addView(matchInfo)
                }
            }

        }



    }





    fun inflateMatchInfo(match: Match, matchInfo: View, specific: Boolean) {
        val matchDate = match.date.toLocalDate()
        val matchTime = match.date.toLocalTime()
        val matchDay = matchDate.format(DateTimeFormatter.ofPattern("dd/MM"))
        val matchHour = matchTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val timeField = matchInfo.findViewById<TextView>(R.id.matchStartingTime)
        timeField.text = "$matchDay${if (specific) " $matchHour" else ""}"

        val localTeam = matchService.getTeam(match.local_team_id)!!
        val visitorTeam = matchService.getTeam(match.visitor_team_id)!!

        val localLogo = matchInfo.findViewById<ImageView>(R.id.localLogo)
        val visitorLogo = matchInfo.findViewById<ImageView>(R.id.visitorLogo)

        loadTeamLogo(localTeam, localLogo)
        loadTeamLogo(visitorTeam, visitorLogo)

    }

    fun addMatchGroupTitle(title: String) {
        // Making the section title
        val matchesTitle = TextView(this)
        matchesTitle.id = View.generateViewId()
        matchesTitle.text = title
        styleDayTitle(matchesTitle)
        matchesList.addView(matchesTitle)
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