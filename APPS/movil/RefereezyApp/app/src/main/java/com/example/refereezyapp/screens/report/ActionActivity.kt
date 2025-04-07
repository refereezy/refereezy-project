package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.OnBackPressedCallback
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.utils.ConfirmationDialog
import com.example.refereezyapp.utils.PopUp

class ActionActivity : _BaseReportActivity() {

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
        val timer = findViewById<TextView>(R.id.timer)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)
        val cardsBtn = findViewById<ImageButton>(R.id.cardsBtn)
        val pauseBtn = findViewById<ImageButton>(R.id.pauseBtn)
        val goalBtn = findViewById<ImageButton>(R.id.goalBtn)
        val warningBtn = findViewById<ImageButton>(R.id.warningBtn)




        // drawing data
        // todo: modify the way to access and count time
        timer.text = "${report.raw.timer[0]}:${report.raw.timer[1]}"

        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()


        // behaviour
        flipBtn.setOnClickListener(this::flipScreen)
        cardsBtn.setOnClickListener { moveTo(CardPickActivity::class.java) }
        goalBtn.setOnClickListener { moveTo(TeamPickActivity::class.java, IncidentType.GOAL) }
        warningBtn.setOnClickListener { moveTo(IncidentActivity::class.java) }
        pauseBtn.setOnClickListener{
            // todo: pause the timer, and the loop to update the database
            PopUp.show(this, "Timer paused/resumed", PopUp.Type.INFO)

        }

        // Disable back button
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

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








}


