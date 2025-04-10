package com.example.rellotgejais.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val nextActivity = findViewById<Button>(R.id.button5)

        nextActivity.setOnClickListener { v: View? ->
            val intent = Intent(this, ActionsActivity::class.java)
            startActivity(intent)
        }
    }
}