package com.example.rellotgejais.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R

class CardPickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_card_pick)
        val tRoja = findViewById<ImageButton>(R.id.redCard)
        val tAmarilla = findViewById<ImageButton>(R.id.yellowCard)

        tRoja.setOnClickListener { v: View? ->
            val intent = Intent(this, TeamPickActivity::class.java)
            startActivity(intent)
        }

        tAmarilla.setOnClickListener { v: View? ->
            val intent = Intent(this, TeamPickActivity::class.java)
            startActivity(intent)
        }
    }
}