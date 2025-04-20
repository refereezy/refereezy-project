package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.refereezyapp.R
import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.LocalStorageManager
import com.example.refereezyapp.data.handlers.ConnectionViewModel
import com.example.refereezyapp.data.handlers.MatchViewModel
import com.example.refereezyapp.data.handlers.RefereeViewModel
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.data.managers.ReportManager
import com.example.refereezyapp.screens.user.LoginActivity
import com.example.refereezyapp.screens.user.MatchActivity

class MainActivity : AppCompatActivity() {

    private val matchViewModel: MatchViewModel by viewModels()
    private val refereeViewModel: RefereeViewModel by viewModels()
    private val apiConnection: ConnectionViewModel by viewModels()

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

        // components
        statusTextContainer = findViewById(R.id.splash_status_container)
        val loadingGif = findViewById<ImageView>(R.id.splash_logo)

        Glide.with(this)
            .load(R.raw.loading_football)
            .into(loadingGif)


        updateLoadingStatus("Testing connection to API")

        var attempts = 0
        val maxAttempts = 7

        // No cargará los datos de la app si no hay conexión a la API
        apiConnection.connectionResult.observe(this) { connected ->

            if (!connected) {
                attempts++
                updateLoadingStatus("Connection to API failed ($attempts/$maxAttempts)")

                if (attempts >= maxAttempts) {
                    updateLoadingStatus("Connection to API failed. Please try again later")
                    return@observe
                }

                apiConnection.testConnection()
                return@observe
            }

            updateLoadingStatus("Connection to API successful")
            loadData()
        }

        apiConnection.testConnection()

    }

    fun loadData() {

        updateLoadingStatus("Loading local storage")
        LocalStorageManager.initialize(this)

        updateLoadingStatus("Loading referee session")
    
        // cargar arbitro de localStorage si hay.
        val refereeId = LocalStorageManager.getRefereeId()
        val refereePass = LocalStorageManager.getRefereeToken()

        if (refereeId == null || refereePass == null) {
            updateLoadingStatus("No referee session found")
            goToLogin()
            return
        }

        updateLoadingStatus("Fetching referee data")


        refereeViewModel.getReferee(refereeId.toInt(), refereePass)

        refereeViewModel.referee.observe(this) { referee ->
            if (referee == null) {
                updateLoadingStatus("Couldnt load referee with id: $refereeId")
                goToLogin()
                return@observe
            }
            RefereeManager.setCurrentReferee(referee)

            updateLoadingStatus("Loading matches")
            // si hay arbitro entonces cargar sus matches
            matchViewModel.loadMatches(referee.id)

            updateLoadingStatus("Checking for previous report")
            loadReport(referee)
        }


    }

    fun loadReport(referee: Referee) {
        updateLoadingStatus("Loading previous report")
        // buscar si hay un reporte pendiente
        FirebaseManager.getReport(referee.id) { report ->
            if (report != null) {
                updateLoadingStatus("Report found with id: ${report.raw.id}")

                ReportManager.setCurrentReport(report)

            } else {
                updateLoadingStatus("No previous report found")
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