package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.example.refereezyapp.MyApp
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.TimerViewModel
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp

class ActionActivity : _BaseReportActivity() {

    private lateinit var flipBtn: ImageButton
    private lateinit var cardsBtn: ImageButton
    private lateinit var pauseBtn: ImageButton
    private lateinit var goalBtn: ImageButton
    private lateinit var warningBtn: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_action)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        flipBtn = findViewById<ImageButton>(R.id.flipBtn)
        cardsBtn = findViewById<ImageButton>(R.id.cardsBtn)
        pauseBtn = findViewById<ImageButton>(R.id.pauseBtn)
        goalBtn = findViewById<ImageButton>(R.id.goalBtn)
        warningBtn = findViewById<ImageButton>(R.id.warningBtn)



        // drawing data



        // behaviour
        flipBtn.setOnClickListener(this::flipScreen)
        cardsBtn.setOnClickListener { moveTo(CardPickActivity::class.java) }
        goalBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.GOAL) }
        warningBtn.setOnClickListener { moveTo(IncidentActivity::class.java) }
        pauseBtn.setOnClickListener(this::toggleTimerState)

        // Disable back button
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }

    fun onBackPress() {

        if (!report.raw.done) {
            ConfirmationDialog.showReportDialog(
                this,
                "Match in progress",
                "You are not supossed to return while the match is in progress",
                onConfirm = {
                    PopUp.show(this, "Back to the match", PopUp.Type.INFO)
                },
                onCancel = {}
            )
        }
    }

    fun toggleTimerState (v: View) {
        if (timerRunning) {
            pauseBtn.setImageResource(android.R.drawable.ic_media_play)
            timerViewModel.stop()

        } else {
            pauseBtn.setImageResource(android.R.drawable.ic_media_pause)
            timerViewModel.play()
        }
        timerRunning = !timerRunning
    }


}


