package com.example.rellotgejais.screens.user

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rellotgejais.R
import com.example.rellotgejais.data.services.LocalStorageService

class WaitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wait)
/**
 * TODO: Requerimientos de emparejamiento con QR
 * - Registrar codigo QR en Socket.io
 * - Esperar a que el arbitro envie sus datos al emparejar el reloj
 * - NO almacenar los datos del arbitro en el reloj
 * - Cuando se tengan los datos del arbitro, hacer procedimiento de buscar reporte.
 * - Si no hay reporte, informar que no hay ningun reporte iniciado.
 * - Mostrar botón para volver a comprobar (tal vez cooldown de 5 segundos?)
 **/


    }
}
