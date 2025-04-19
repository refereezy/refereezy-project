package com.example.rellotgejais.screens.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R
import com.example.rellotgejais.screens.report.ActionsActivity

class MainActivity : AppCompatActivity() {
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

        nextActivity.setOnClickListener { v: View? ->
            val intent = Intent(this, ActionsActivity::class.java)
            startActivity(intent)
        }
    }
}