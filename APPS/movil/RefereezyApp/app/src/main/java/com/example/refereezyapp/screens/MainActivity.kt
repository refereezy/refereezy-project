package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.ConnectionService
import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.LocalStorageManager
import com.example.refereezyapp.data.MatchService
import com.example.refereezyapp.data.RefereeService
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import com.example.refereezyapp.data.static.ReportManager
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val matchService: MatchService by viewModels()
    private val refereeService: RefereeService by viewModels()
    private val connectionService: ConnectionService by viewModels()

    private lateinit var statusText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        statusText = findViewById(R.id.splash_status_text)

        updateLoadingStatus("Loading local storage")

        LocalStorageManager.initialize(this)

        updateLoadingStatus("Testing connection to API")
        // prueba de conexion
        if (!connectionService.testConnection()) {
            goToLogin()
            return
        }

        updateLoadingStatus("Loading referee session")

        // cargar arbitro de localStorage si hay.
        val refereeId = LocalStorageManager.getRefereeId()
        val refereePass = LocalStorageManager.getRefereePass()

        if (refereeId == null || refereePass == null) {
            goToLogin()
            return
        }

        updateLoadingStatus("Fetching referee data")

        val referee = refereeService.getReferee(refereeId.toInt(), refereePass)

        // si no hay arbitro entonces redirigir a login
        if (referee == null) {
            goToLogin()
            return
        }

        RefereeManager.setCurrentReferee(referee)

        updateLoadingStatus("Loading matches")
        // si hay arbitro entonces cargar sus matches
        matchService.loadMatches(referee.id)


        updateLoadingStatus("Loading previous report")
        // buscar si hay un reporte pendiente
        runBlocking {
            val report = async { FirebaseManager.getReport(referee.id) }.await()
            if (report != null) {
                val populatedMatch = async { matchService.populateMatch(report.match_id!!) }.await()

                if (populatedMatch == null) return@runBlocking

                val populatedReport = PopulatedReport(report, populatedMatch)

                MatchManager.setCurrentMatch(populatedMatch)
                ReportManager.setCurrentReport(populatedReport)
            }
        }

        updateLoadingStatus("Starting app")

        val intent = Intent(this@MainActivity, MatchActivity::class.java)
        startActivity(intent)

    }

    fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun updateLoadingStatus(status: String) {
        statusText.text = status
    }
}