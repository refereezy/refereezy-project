package com.example.rellotgejais.screens.report

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R
import com.example.rellotgejais.models.IncidentType
import kotlin.text.replace

class IncidentActivity : _BaseReportActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incidence)

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val lesionBtn = findViewById<ImageButton>(R.id.lesionBtn)
        val fightBtn = findViewById<ImageButton>(R.id.fightBtn)
        val suspendBtn = findViewById<ImageButton>(R.id.suspendBtn)


        // behaviour
        lesionBtn.setOnClickListener {
            moveTo(MicroPhoneActivity::class.java, IncidentType.LESION)
        }

        fightBtn.setOnClickListener {
            moveTo(MicroPhoneActivity::class.java, IncidentType.FIGHT)
        }

        suspendBtn.setOnClickListener {
            moveTo(MicroPhoneActivity::class.java, IncidentType.SUSPEND)
        }

    }


    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }
}