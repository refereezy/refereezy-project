package com.example.rellotgejais.screens.report

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.rellotgejais.R
import com.example.rellotgejais.models.IncidentType
import com.example.rellotgejais.models.Team
import com.example.rellotgejais.models.TeamType
import kotlin.text.replace

class TeamPickActivity : _BaseReportActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_team_pick)

        //! this class extends BaseReportActivity, which initializes the basic values

        // getting previous data
        val type = intent.getSerializableExtra("type") as IncidentType // to ensure there is a type before continueing

        // components
        val localBtn = findViewById<ImageButton>(R.id.localBtn)
        val visitorBtn = findViewById<ImageButton>(R.id.visitorBtn)


        // drawing data

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()

        loadTeamLogo(localTeam, localBtn)
        loadTeamLogo(visitorTeam, visitorBtn)

        // behaviour
        localBtn.setOnClickListener { moveTo(PlayerPickActivity::class.java, TeamType.LOCAL) }
        visitorBtn.setOnClickListener { moveTo(PlayerPickActivity::class.java, TeamType.VISITOR) }

    }


    fun loadTeamLogo(team: Team, imageView: ImageView) {
        Glide.with(this)
            .load(team.logo_url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
            .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))
    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }
}