package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.OnBackPressedCallback
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.managers.ReportManager
import com.example.refereezyapp.screens.user.MatchActivity
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp

class ActionActivity : _BaseReportActivity() {

    private lateinit var flipBtn: ImageButton
    private lateinit var cardsBtn: ImageButton
    private lateinit var pauseBtn: ImageButton
    private lateinit var goalBtn: ImageButton
    private lateinit var incidentButton: ImageButton


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
        incidentButton = findViewById<ImageButton>(R.id.warningBtn)



        // drawing data
        // nothing to draw rn


        // behaviours
        flipBtn.setOnClickListener(this::flipScreen)
        cardsBtn.setOnClickListener { moveTo(CardPickActivity::class.java) }
        goalBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.GOAL) }

        // incidentBtn behaviours
        incidentButton.setOnClickListener { moveTo(IncidentActivity::class.java) }
        incidentButton.setOnLongClickListener { moveTo(IncidentListActivity::class.java); true }

        // pauseBtn behaviours
        pauseBtn.setOnClickListener(this::toggleTimerState)
        pauseBtn.setOnLongClickListener { tryCloseReport(); true}

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
        else {
            moveTo(MatchActivity::class.java)
        }

    }

    fun toggleTimerState (v: View) {
        if (timer.isRunning) {
            pauseBtn.setImageResource(android.R.drawable.ic_media_play)
            timer.stop()
        } else {
            pauseBtn.setImageResource(android.R.drawable.ic_media_pause)
            timer.play()
        }
    }

    fun tryCloseReport () {
        // alertar que intenta acabar el partido
        ConfirmationDialog.showReportDialog(
            this,
            "End match",
            "Are you sure you want to end the match?",
            onConfirm = this::endMatchReport,
            onCancel = {}
        )
    }



}


