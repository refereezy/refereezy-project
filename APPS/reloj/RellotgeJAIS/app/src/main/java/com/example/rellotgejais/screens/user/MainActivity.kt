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
import com.example.rellotgejais.data.handlers.RefereeService
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
    private val refereeViewModel: RefereeViewModel by viewModels()

    private lateinit var nextActivity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // components
        nextActivity = findViewById<TextView>(R.id.nextActivity)
        val loadingGif = findViewById<ImageView>(R.id.splash_logo)
        Glide.with(this)
            .load(R.raw.loading_football)
            .into(loadingGif)

        LocalStorageService.initialize(this)
        SocketService.connect()

        val refereeId = LocalStorageService.getRefereeId()
        val token = LocalStorageService.getRefereeToken()

        println("refereeId: $refereeId")
        println("token: $token")


        if (refereeId != null && token != null) {
            refereeViewModel.getReferee(refereeId.toInt(), token)
        }
        else {
            checkQrCode()
        }

        refereeViewModel.referee.observe(this) { referee ->
            if (referee == null) {
                checkQrCode()
                return@observe
            }
            RefereeManager.setCurrentReferee(referee)

            val intent = Intent(this, WaitActivity::class.java)
            startActivity(intent)
            finish()
        }



        //API QR
        clockViewModel.clock.observe(this) { code ->
            if (code != null) {
                nextActivity.visibility = View.VISIBLE
                nextActivity.setOnClickListener { v: View? ->
                    val intent = Intent(this, QrActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

    }

    private fun checkQrCode() {
        val qrCode = LocalStorageService.getClockQrCode()

        if (qrCode != null) {
            println("QR code found: $qrCode")
            nextActivity.visibility = View.VISIBLE
            nextActivity.setOnClickListener { v: View? ->
                val intent = Intent(this, QrActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else {
            clockViewModel.generateCode()
        }
    }
}