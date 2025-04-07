package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.screens.fragments.ScoreFragment

class CardPickActivity : _BaseReportActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_pick)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)
        val yellowCardBtn = findViewById<ImageButton>(R.id.yellowCardBtn)
        val redCardBtn = findViewById<ImageButton>(R.id.redCardBtn)
        val timer = findViewById<TextView>(R.id.timer)

        // drawing data
        // todo: modify the way to access and count time
        timer.text = "${report.raw.timer[0]}:${report.raw.timer[1]}"

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()

        // behaviour
        flipBtn.setOnClickListener(this::flipScreen)
        yellowCardBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.YELLOW_CARD) }
        redCardBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.RED_CARD) }


    }


}