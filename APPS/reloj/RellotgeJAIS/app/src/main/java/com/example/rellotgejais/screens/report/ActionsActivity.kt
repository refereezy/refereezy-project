package com.example.rellotgejais.screens.report

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.SocketHandler
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
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

        // Disable back button
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = true
                Log.d("ActionsActivity", "Back button pressed")
                onBackPress()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

    }

    fun onBackPress() {
        ConfirmationDialog.showReportDialog(
            this,
            "Are you sure you wanna leave this report unfinished?",
            onConfirm = this::finish,
            onCancel = {}
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ReportManager.clearReport()
        timer.resetTimer()
        val qr = LocalStorageService.getClockQrCode()?: run { return }
        socketHandler.unlinkReport(qr, report.raw.id)
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