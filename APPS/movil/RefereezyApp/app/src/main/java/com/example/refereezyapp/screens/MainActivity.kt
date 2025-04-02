package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import android.widget.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val matchService: MatchService by viewModels()
    private val refereeService: RefereeService by viewModels()
    private val connectionService: ConnectionService by viewModels()

    private lateinit var statusTextContainer: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        statusTextContainer = findViewById(R.id.splash_status_container)


        updateLoadingStatus("Testing connection to API")

        // No cargará los datos de la app si no hay conexión a la API

        CoroutineScope(Dispatchers.IO).launch {

            var attempts = 0
            val maxAttempts = 7

          while (!connectionService.testConnection()) {
              attempts++
              runOnUiThread { updateLoadingStatus("Connection to API failed ($attempts/$maxAttempts)") }

              if (attempts >= maxAttempts) {
                  runOnUiThread { updateLoadingStatus("Connection to API failed. Please try again later") }
                  return@launch
              }

          }

              runOnUiThread {
                loadData()
              }

        }

    }

    fun loadData() {

        updateLoadingStatus("Loading local storage")
        LocalStorageManager.initialize(this)

        updateLoadingStatus("Loading referee session")

        // cargar arbitro de localStorage si hay.
        val refereeId = LocalStorageManager.getRefereeId()
        val refereePass = LocalStorageManager.getRefereePass()

        if (refereeId == null || refereePass == null) {
            updateLoadingStatus("No referee session found")
            goToLogin()
            return
        }

        updateLoadingStatus("Fetching referee data")

        val referee = refereeService.getReferee(refereeId.toInt(), refereePass)

        // si no hay arbitro entonces redirigir a login
        if (referee == null) {
            updateLoadingStatus("Couldnt load referee with id: $refereeId")
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

                updateLoadingStatus("Report found with id: ${report.id}")

                MatchManager.setCurrentMatch(populatedMatch)
                ReportManager.setCurrentReport(populatedReport)
            } else {
                updateLoadingStatus("No previous report found")
            }
        }

        updateLoadingStatus("Starting app")

        // add button to redirect to match activity
        val button = Button(this)
        button.text = "Start"
        button.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
            finish()
        }
        statusTextContainer.addView(button)

    }


    fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun updateLoadingStatus(status: String) {
        Log.d("MainActivity", status)
        // Agrega un texto al contenedor de estados/logs

        val textView = android.widget.TextView(this@MainActivity)
        textView.text = status
        statusTextContainer.addView(textView)
    }
}