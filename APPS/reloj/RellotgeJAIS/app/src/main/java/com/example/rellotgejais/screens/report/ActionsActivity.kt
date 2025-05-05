package com.example.rellotgejais.screens.report

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import com.example.rellotgejais.R
import com.example.rellotgejais.models.IncidentType
import com.example.rellotgejais.utils.ConfirmationDialog

class ActionsActivity : _BaseReportActivity() {

    private lateinit var cardsBtn: ImageButton
    private lateinit var pauseBtn: ImageButton
    private lateinit var goalBtn: ImageButton
    private lateinit var incidentButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accions)


        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        cardsBtn = findViewById<ImageButton>(R.id.cardsBtn)
        pauseBtn = findViewById<ImageButton>(R.id.pauseBtn)
        goalBtn = findViewById<ImageButton>(R.id.goalBtn)
        incidentButton = findViewById<ImageButton>(R.id.warningBtn)

        // drawing data
        // nothing to draw rn


        // behaviours
        cardsBtn.setOnClickListener { moveTo(CardPickActivity::class.java) }
        goalBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.GOAL) }

        // incidentBtn behaviours
        incidentButton.setOnClickListener { moveTo(IncidentActivity::class.java) }
        incidentButton.setOnLongClickListener { moveTo(IncidentListActivity::class.java); true }

        // pauseBtn behaviours
        pauseBtn.setOnClickListener(this::toggleTimerState)
        pauseBtn.setOnLongClickListener { tryCloseReport(); true}


    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
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
            "Are you sure you want to end the match?",
            onConfirm = this::endMatchReport,
            onCancel = {}
        )
    }
}