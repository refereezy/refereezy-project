package com.example.rellotgejais.screens.report

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rellotgejais.R
import com.example.rellotgejais.data.handlers.ReportHandler
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.models.IncidentType
import com.example.rellotgejais.models.IncidentType.*
import java.util.Locale

class MicroPhoneActivity : _BaseReportActivity() {
    private var description: String? = null
    private var micState: Boolean = false
    private val REQUEST_AUDIO_PERMISSION = 1
    private lateinit var speechRecognitionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_micro_phone)

        //Demanar permisos de audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermiso()
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // data
        val type = intent.getSerializableExtra("type") as IncidentType
        val requiresPlayer: Boolean = when (type) {
            FIGHT, LESION, SUSPEND, OTHER -> false
            GOAL, YELLOW_CARD, RED_CARD -> true
        }
        // components
        val continueBtn = findViewById<ImageButton>(R.id.continueBtn)
        val micbtn = findViewById<ImageButton>(R.id.micBtn)

        // behaviour

        // Initialize the ActivityResultLauncher
        speechRecognitionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (matches != null) {
                    description = matches[0]
                }
            }
        }

        micbtn.setOnClickListener { view ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (!micState) {
                    micbtn.setImageResource(R.drawable.micred)
                    micState = true
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }

                    try {
                        speechRecognitionLauncher.launch(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this, "No speech recognition service found", Toast.LENGTH_SHORT).show()
                    }
                    //speechRecognizer.startListening(speechIntent)
                } else {
                    micbtn.setImageResource(R.drawable.mic)
                    micState = false
                }
            } else {
                pedirPermiso() // Si no hay permisos, pedirlos de nuevo
            }
        }

        continueBtn.setOnClickListener {
            if (description.isNullOrBlank() && !requiresPlayer) {
                Toast.makeText(this, "You must specify a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (requiresPlayer) {
                if (description.isNullOrBlank()) {
                    Toast.makeText(this, "You should specify a description", Toast.LENGTH_SHORT).show()
                }
                moveTo(TeamPickActivity::class.java, description)
            } else {
                val incident = Incident(
                    type = type,
                    description = description ?: "", // Evita el !! y usa un valor por defecto
                    minute = timer.getElapsedMinutes(),
                )

                ReportHandler.addIncident(report, incident)
                socketHandler.notifyNewIncident(report.raw.id, incident.id)

                if (type == SUSPEND) {
                    endMatchReport()
                    finish()
                    return@setOnClickListener
                }

                moveTo(ActionsActivity::class.java)
                finish()
            }
        }
    }

    private fun pedirPermiso() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION
        )
    }

    // Manejar la respuesta del usuario cuando acepta o rechaza los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Debes conceder permisos para usar la app", Toast.LENGTH_LONG)
                    .show()
                pedirPermiso() // Volver a pedirlo si el usuario lo rechaza
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // re dibuja la puntuacion incluso cuando se cambia de activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.scoreboard, scoreboard)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
