package com.example.refereezyapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.data.FirebaseManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        println("Probando firebase")

        FirebaseManager.getReport(1) {
            matchReport, error ->
            if (matchReport != null) {
                println("Acta encontrada: ${matchReport.matchId}")

                for (incidencia in matchReport.incidents) {
                    println("Incidencia encontrada: ${incidencia.description}")
                }

            } else {
                println("ERROR: $error")
            }
        }



        //startActivity(Intent(this, PlayerPickActivity::class.java))
    }
}