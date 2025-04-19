package com.example.rellotgejais.screens.report

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.models.TimerViewModel

class ActionsActivity : AppCompatActivity() {
    var timerState: Boolean = false
    private lateinit var timer: TimerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accions)


        timer = (application as MyApp).timerViewModel
        val buttonPlayPause = findViewById<ImageButton>(R.id.PausaStart)
        val btnTarjetas = findViewById<ImageButton>(R.id.tarjetas)
        val btnIncidencias = findViewById<ImageButton>(R.id.inccidencias)
        val btnGol = findViewById<ImageButton>(R.id.gol)
        buttonPlayPause.setOnClickListener { v: View? ->
            if (timerState) {
                buttonPlayPause.setImageResource(android.R.drawable.ic_media_play)
                timer.stopTimer()

            } else {
                buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                timer.startTimer()
            }
            timerState = !timerState
        }



        btnGol.setOnClickListener { v: View? ->
            val intentt = Intent(this, TeamPickActivity::class.java)
            startActivity(intentt)
        }
        btnTarjetas.setOnClickListener { v: View? ->
            val intent = Intent(this, CardPickActivity::class.java)
            startActivity(intent)
        }
        btnIncidencias.setOnClickListener { v: View? ->
            val intent = Intent(this, IncidenceActivity::class.java)
            startActivity(intent)
        }

    }
}