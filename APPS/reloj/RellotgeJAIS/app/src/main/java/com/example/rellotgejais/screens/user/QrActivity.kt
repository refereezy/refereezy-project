package com.example.rellotgejais.screens.user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rellotgejais.R

class QrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qr)
/**
 * TODO: Requerimientos de emparejamiento con QR
 * - Pedir QR a la api
 * - Almacenar QR en el reloj
 * - Mostrar QR en la api.
 **/
    }
}