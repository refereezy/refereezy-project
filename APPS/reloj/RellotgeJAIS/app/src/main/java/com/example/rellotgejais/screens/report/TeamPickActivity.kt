package com.example.rellotgejais.screens.report

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R

class TeamPickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_team_pick)

        val local = findViewById<ImageButton>(R.id.localteam)
        val visitant = findViewById<ImageButton>(R.id.visitorteam)

        local.setOnClickListener { v: View? ->
            val intent = Intent(this, PlayerPickActivity::class.java)
            startActivity(intent)
        }
        /*visitant.setOnClickListener { v: View? ->
            val intent = Intent(this, vititantPlayers::class.java)
            startActivity(intent)
        }*/
    }
}