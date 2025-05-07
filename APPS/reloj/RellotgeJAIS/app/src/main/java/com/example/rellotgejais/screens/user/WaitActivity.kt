package com.example.rellotgejais.screens.user

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.MyApp
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.data.services.SocketService
import com.example.rellotgejais.screens.report.ActionsActivity
import com.example.rellotgejais.utils.ConfirmationDialog
import kotlinx.coroutines.runBlocking

class WaitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wait)

        SocketService.awaitReport()
        SocketService.newReport.observe(this) {
            if (it == 0) return@observe
            loadReport()
        }

        val unpairBtn = findViewById<ImageButton>(R.id.unpairBtn)
        unpairBtn.setOnClickListener {
            ConfirmationDialog.showReportDialog(
                this,
                "Unpair referee with DNI: ${RefereeManager.getCurrentReferee()?.dni}",
                onConfirm = {
                    LocalStorageService.clearRefereeReference()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onCancel = {}
            )
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
        val intent = Intent(this, ActionsActivity::class.java)
        startActivity(intent)

    }
}
