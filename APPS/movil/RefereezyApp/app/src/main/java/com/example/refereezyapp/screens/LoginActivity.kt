package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.refereezyapp.R
import com.example.refereezyapp.data.LocalStorageManager
import com.example.refereezyapp.data.MatchService
import com.example.refereezyapp.data.RefereeService
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.static.RefereeManager

class LoginActivity : AppCompatActivity() {

    private val refereeService: RefereeService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Carga con Glide el logo de Refereezy
        Glide.with(this)
            .load("URL_DEL_LOGO") // TODO: Definir una URL para el logo
            .into(findViewById(R.id.logo))

        // campos de login
        val emailField = findViewById<EditText>(R.id.dniField)
        val passwordField = findViewById<EditText>(R.id.passwordField)

        // logica de login
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            login(emailField, passwordField)
        }


    }

    fun login (emailField: EditText, passwordField: EditText) {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        // requiere los campos
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            emailField.error = "Email is required"
            passwordField.error = "Password is required"
            return
        }

        // valida credenciales
        val credentials = RefereeLogin(email, password)
        val referee = refereeService.login(credentials)

        if (referee == null) {
            emailField.error = "Invalid email"
            passwordField.error = "Invalid password"
            return
        }
        // guarda el referee
        RefereeManager.setCurrentReferee(referee)

        // guarda el referee en localStorage
        LocalStorageManager.saveRefereeData(referee.id.toString(), password)

        // redirige a la pantalla de matches
        val intent = Intent(this, MatchActivity::class.java)
        startActivity(intent)
        finish()

    }
}