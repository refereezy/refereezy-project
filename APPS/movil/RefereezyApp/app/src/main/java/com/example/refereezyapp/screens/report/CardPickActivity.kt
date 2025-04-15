package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.example.refereezyapp.MyApp
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.TimerViewModel
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


        // drawing data

        // behaviour
        flipBtn.setOnClickListener(this::flipScreen)
        yellowCardBtn.setOnClickListener { moveTo(MicrophoneActivity::class.java, IncidentType.YELLOW_CARD) }
        redCardBtn.setOnClickListener { moveTo(MicrophoneActivity::class.java, IncidentType.RED_CARD) }


    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }


}