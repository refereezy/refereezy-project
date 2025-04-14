package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.IncidentType.*
import com.example.refereezyapp.utils.PopUp

class MicrophoneActivity : _BaseReportActivity() {

    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_microphone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // data
        val type = intent.getSerializableExtra("type") as IncidentType
        val requiresPlayer: Boolean = when (type){
            FIGHT, LESION, SUSPEND, OTHER -> false
            GOAL, YELLOW_CARD, RED_CARD -> true
        }

        // components
        // val micBtn = findViewById<ImageButton>(R.id.micBtn)
        val continueBtn = findViewById<ImageButton>(R.id.continueBtn)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)

        // behaviour

        // logica para manejar microfono
        // todo: el microfono debe setear la propiedad description

        continueBtn.setOnClickListener {
            if (description.isNullOrBlank() && !requiresPlayer) {
                PopUp.show(this, "You must specify a description", PopUp.Type.ERROR)
                return@setOnClickListener
            }

            if (requiresPlayer) {
                if (description.isNullOrBlank()) PopUp.show(this, "You should specify a description", PopUp.Type.INFO)
                moveTo(TeamPickActivity::class.java, description)
            }
            else {
                val incident = Incident(
                    type = type,
                    description = description!!,
                    minute = timer.getElapsedMinutes(),
                )

                // this automatically modifies the report and saves into database
                ReportService.addIncident(report, incident)

                if (type == SUSPEND) {
                    endMatchReport()
                    finish()
                    return@setOnClickListener
                }

                moveTo(ActionActivity::class.java)
                finish()
            }
        }

        flipBtn.setOnClickListener(this::flipScreen)

    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }
}