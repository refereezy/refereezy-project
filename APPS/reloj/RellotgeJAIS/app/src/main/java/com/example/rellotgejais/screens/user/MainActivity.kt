package com.example.rellotgejais.screens.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.RefereeViewModel
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.screens.report.ActionsActivity
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val refereeViewModel: RefereeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val nextActivity = findViewById<Button>(R.id.nextActivity)


        // components
        val loadingGif = findViewById<ImageView>(R.id.splash_logo)
        Glide.with(this)
            .load(R.raw.loading_football)
            .into(loadingGif)


        /**
         * TODO: Requerimientos de emparejamiento con QR
         * Cargar datos de si esta vinculado
         * pedir a la api el referee
         */

        val tempToken = "$2b$12$/88liRIt9od1TfvQDqIYj.3B3KdN0ZAv/gfNrfjXN/8aB1TPejxa."
        val tempId = 1

        refereeViewModel.referee.observe(this) { referee ->
            if (referee != null) {
                RefereeManager.setCurrentReferee(referee)
                loadReport()
            }
        }

        nextActivity.setOnClickListener { v: View? ->
            refereeViewModel.getReferee(tempId, tempToken)
        }
    }

    fun loadReport() {
        val referee = RefereeManager.getCurrentReferee()!!
        runBlocking {
            val report = ReportHandler.getReport(referee.id)
            if (report == null) {
                println("No report found")
                return@runBlocking
            }
            println("Report found: ${report.raw.id}")
            ReportManager.setCurrentReport(report)

        }

        val report = ReportManager.getCurrentReport()!!
        val timerViewModel = (application as MyApp).timerViewModel
        timerViewModel.initTimer(report.raw.timer[0], report.raw.timer[1])
        val intent = Intent(this@MainActivity, ActionsActivity::class.java)
        startActivity(intent)

    }
    fun goToLogin() {
        val intent = Intent(this, WaitActivity::class.java)
        startActivity(intent)
        finish()
    }
}