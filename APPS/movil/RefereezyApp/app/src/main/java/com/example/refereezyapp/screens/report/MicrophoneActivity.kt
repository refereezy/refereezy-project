package com.example.refereezyapp.screens.report

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.ReportService
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.IncidentType
import com.example.refereezyapp.data.models.IncidentType.*
import com.example.refereezyapp.utils.PopUp
import java.util.Locale

class MicrophoneActivity : _BaseReportActivity() {

    private var description: String? = null
    private var miceStatse: Boolean = false
    private val REQUEST_AUDIO_PERMISSION = 1
    private lateinit var speechRecognizer: SpeechRecognizer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_microphone)

        //Demanar permisos de audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            pedirPermiso()
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // data
        val type = intent.getSerializableExtra("type") as IncidentType
        val requiresPlayer: Boolean = when (type){
            FIGHT, LESION, SUSPEND, OTHER -> false
            GOAL, YELLOW_CARD, RED_CARD -> true
        }

        // components
        val continueBtn = findViewById<ImageButton>(R.id.continueBtn)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)
        val micTextBoard = findViewById<TextView>(R.id.micTextBoard)
        val micbtn = findViewById<ImageButton>(R.id.micBtn)

        // behaviour

        // logica para manejar microfono
        // todo: el microfono debe setear la propiedad description

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        // Crear intent de reconocimiento de voz
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        // Configurar el reconocimiento de voz
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(this@MicrophoneActivity, "Escuchando...", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Toast.makeText(this@MicrophoneActivity, "Error en reconocimiento: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    micTextBoard.text = matches[0] // Mostrar el texto reconocido
                    description = matches[0]
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        micbtn.setOnClickListener {view ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
                if (!miceStatse) {
                    micbtn.setImageResource(R.drawable.micred)
                    miceStatse = true
                    speechRecognizer.startListening(speechIntent)
                }else{
                    micbtn.setImageResource(R.drawable.mic)
                    miceStatse = false
                }
            } else {
                pedirPermiso() // Si no hay permisos, pedirlos de nuevo
            }
        }

        continueBtn.setOnClickListener {
            if (description.isNullOrBlank() && !requiresPlayer) {
                PopUp.show(this, "You must specify a description", PopUp.Type.ERROR)
                return@setOnClickListener
            }

            if (requiresPlayer) {
                if (description.isNullOrBlank()) PopUp.show(this, "You should specify a description", PopUp.Type.INFO)
                moveTo(TeamPickActivity::class.java, description)
            }
            else {
                val incident = Incident(
                    type = type,
                    description = description!!,
                    minute = timer.getElapsedMinutes(),
                )

                // this automatically modifies the report and saves into database
                ReportService.addIncident(report, incident)

                if (type == SUSPEND) {
                    endMatchReport()
                    finish()
                    return@setOnClickListener
                }

                moveTo(ActionActivity::class.java)
                finish()
            }
        }

        flipBtn.setOnClickListener(this::flipScreen)

    }
    private fun pedirPermiso() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION)
    }
    // Manejar la respuesta del usuario cuando acepta o rechaza los permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Debes conceder permisos para usar la app", Toast.LENGTH_LONG).show()
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
        speechRecognizer.destroy()
    }

}