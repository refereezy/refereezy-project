package com.example.refereezyapp.screens

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.refereezyapp.R
import com.example.refereezyapp.data.MatchService
import com.example.refereezyapp.data.models.Match
import java.time.format.DateTimeFormatter
import com.example.refereezyapp.data.static.MatchManager
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty

class MatchActivity : AppCompatActivity() {

    private val matchService: MatchService by viewModels()

    var matches: List<Match> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_match)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        loadMatches()


        // then each match in this week are loaded using the layout @layout/layout_match_info

        // then the matches more than a week from now are shown




    }


    fun loadMatches() {

        matches = MatchManager.getMatches()

        // Agrupa las partidas por fecha
        val groupedMatches: Map<String, List<Match>> = matches.groupBy { match ->
            val matchDate = match.date.toLocalDate()
            val today = java.time.LocalDate.now()
            val oneWeekFromToday = today.plusWeeks(1)

            when {
                matchDate.isBefore(today) -> "Past"
                matchDate.isAfter(oneWeekFromToday) -> "More than a week from now" //matches more than a week
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM")
                    matchDate.format(formatter)
                }
            }
        }

        // loading data to the linear layout with id matchesList
        val matchesList = findViewById<LinearLayout>(R.id.matchesList)

        // first the past matches are shown using a TextView with the style="@style/MatchDayTitle"
        val pastMatches = groupedMatches["Past"] ?: emptyList()
        if (pastMatches.isNotEmpty()) {
            val matchesTitle = TextView(this)
            matchesTitle.id = View.generateViewId()
            matchesTitle.text = "Past"
            styleDayTitle(matchesTitle)
            matchesList.addView(matchesTitle)

            pastMatches.forEach { match ->
                val matchInfo = layoutInflater.inflate(R.layout.layout_match_info, null)

                val matchDate = match.date.toLocalDate()
                val matchTime = match.date.toLocalTime()
                val matchDay = matchDate.format(DateTimeFormatter.ofPattern("dd/MM"))
                val matchHour = matchTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                matchInfo.findViewById<TextView>(R.id.matchStartingTime).text = "$matchDay $matchHour"

                val localTeam = matchService.getTeam(match.local_team_id)!!
                val visitorTeam = matchService.getTeam(match.visitor_team_id)!!

                Glide.with(this)
                    .load(localTeam.logo_url)
                    .into(matchInfo.findViewById<ImageView>(R.id.localLogo))
                    .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))

                Glide.with(this)
                    .load(visitorTeam.logo_url)
                    .into(matchInfo.findViewById<ImageView>(R.id.visitorLogo))
                    .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))

                matchesList.addView(matchInfo)
            }

        }


    }



    private fun styleDayTitle(title: TextView) {
        title.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = 50.toDp()
            topMargin = 30.toDp()
        }
        title.textSize = 28f
        title.typeface = ResourcesCompat.getFont(this, R.font.k2d_medium)
    }

    fun Int.toDp(): Int = (this * resources.displayMetrics.density).toInt()

}