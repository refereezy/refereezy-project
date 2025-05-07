package com.example.rellotgejais.screens.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ClockHandler
import com.example.rellotgejais.data.handlers.RefereeViewModel
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.data.services.SocketService
import com.example.rellotgejais.screens.report.ActionsActivity
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val clockViewModel: ClockHandler by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val nextActivity = findViewById<TextView>(R.id.nextActivity)


        // components
        val loadingGif = findViewById<ImageView>(R.id.splash_logo)
        Glide.with(this)
            .load(R.raw.loading_football)
            .into(loadingGif)

        LocalStorageService.initialize(this)
        SocketService.connect()

        val refereeId = LocalStorageService.getRefereeId()
        val token = LocalStorageService.getRefereeToken()


        if (refereeId != null && token != null) {
            val intent = Intent(this, WaitActivity::class.java)
            startActivity(intent)
            finish()
        }

        val qrCode = LocalStorageService.getClockQrCode()

        if (qrCode != null) {
            val intent = Intent(this, QrActivity::class.java)
            startActivity(intent)
            finish()
        }

        //API QR
        clockViewModel.generateCode()
        clockViewModel.clock.observe(this) { code ->
            if (code != null) {
                nextActivity.setOnClickListener { v: View? ->
                    val intent = Intent(this, QrActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

    }
}