package com.example.rellotgejais.screens.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
        val nextActivity = findViewById<Button>(R.id.startBtn)


        /**
         * TODO: Requerimientos de emparejamiento con QR
         * - Pedir QR a la api
         * - Almacenar QR en el reloj
         * - Mostrar QR en la api.
         * - Registrar codigo QR en Socket.io
         * - Esperar a que el arbitro envie sus datos al emparejar el reloj
         * - NO almacenar los datos del arbitro en el reloj
         * - Cuando se tengan los datos del arbitro, hacer procedimiento de buscar reporte.
         * - Si no hay reporte, informar que no hay ningun reporte iniciado.
         * - Mostrar botón para volver a comprobar (tal vez cooldown de 5 segundos?)
         */

        val tempToken = "$2b$12$/88liRIt9od1TfvQDqIYj.3B3KdN0ZAv/gfNrfjXN/8aB1TPejxa."
        val tempId = 1

        refereeViewModel.getReferee(tempId, tempToken)
        refereeViewModel.referee.observe(this) { referee ->
            if (referee != null) {
                RefereeManager.setCurrentReferee(referee)
                loadReport()
            }
        }



        nextActivity.setOnClickListener { v: View? ->
            val intent = Intent(this, ActionsActivity::class.java)
            startActivity(intent)
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
}