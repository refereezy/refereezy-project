package com.example.refereezyapp.screens.report

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.models.IncidentType

class IncidentActivity : _BaseReportActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incident)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)
        val lesionBtn = findViewById<ImageButton>(R.id.lesionBtn)
        val fightBtn = findViewById<ImageButton>(R.id.fightBtn)
        val suspendBtn = findViewById<ImageButton>(R.id.suspendBtn)


        // behaviour
        lesionBtn.setOnClickListener {
            moveTo(MicrophoneActivity::class.java, IncidentType.LESION)
        }

        fightBtn.setOnClickListener {
            moveTo(MicrophoneActivity::class.java, IncidentType.FIGHT)
        }

        suspendBtn.setOnClickListener {
            moveTo(MicrophoneActivity::class.java, IncidentType.SUSPEND)
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