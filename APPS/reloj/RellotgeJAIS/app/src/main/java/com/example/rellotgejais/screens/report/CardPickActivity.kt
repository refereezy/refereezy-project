package com.example.rellotgejais.screens.report

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R
import com.example.rellotgejais.models.IncidentType
import kotlin.text.replace

class CardPickActivity : _BaseReportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_card_pick)

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val yellowCardBtn = findViewById<ImageButton>(R.id.yellowCard)
        val redCardBtn = findViewById<ImageButton>(R.id.redCard)


        // drawing data

        // behaviour
        yellowCardBtn.setOnClickListener {
            moveTo(
                MicroPhoneActivity::class.java,
                IncidentType.YELLOW_CARD
            )
        }
        redCardBtn.setOnClickListener {
            moveTo(
                MicroPhoneActivity::class.java,
                IncidentType.RED_CARD
            )
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