package com.example.refereezyapp.screens.user

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.services.FirebaseService.getDoneReports
import com.example.refereezyapp.utils.PDFUtils
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope // Importar para usar lifecycleScope
import com.example.refereezyapp.data.managers.RefereeManager

class ActaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acta)

        val container = findViewById<LinearLayout>(R.id.laterMatches)
        val referee = RefereeManager.getCurrentReferee()!!
        // Llamar a la función suspendida dentro de una corrutina
        lifecycleScope.launch {
            getDoneReports(referee.id) { reports -> // pon aquí el ID real
                // Ejecutar en el hilo principal (UI) para modificar la interfaz
                runOnUiThread {
                    reports.forEach { report ->
                        val itemView = LayoutInflater.from(this@ActaActivity)
                            .inflate(R.layout.layout_endedmatch_info, container, false)

                        // Aquí puedes setear logos, textos, etc. si quieres

                        val btn = itemView.findViewById<ImageView>(R.id.pdfgenerate)
                        btn.setOnClickListener {
                            // Llamada correcta a las funciones estáticas de PDFUtils
                            val pdfFile = PDFUtils.generateActaPdf(this@ActaActivity, report)
                            PDFUtils.openPdfFile(this@ActaActivity, pdfFile)
                        }

                        container.addView(itemView)
                    }
                }
            }
        }
    }
}
