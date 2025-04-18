package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.models.TeamType
import com.example.refereezyapp.utils.PopUp

class TeamPickActivity : _BaseReportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_team_pick)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //! this class extends BaseReportActivity, which initializes the basic values

        // getting previous data
        val type = intent.getSerializableExtra("type") as IncidentType // to ensure there is a type before continueing

        // components
        val localBtn = findViewById<ImageButton>(R.id.localBtn)
        val visitorBtn = findViewById<ImageButton>(R.id.visitorBtn)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)


        // drawing data

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()

        loadTeamLogo(localTeam, localBtn)
        loadTeamLogo(visitorTeam, visitorBtn)

        // behaviour
        flipBtn.setOnClickListener(this::flipScreen)
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