package com.example.refereezyapp.screens.user

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
import com.example.refereezyapp.data.services.LocalStorageService
import com.example.refereezyapp.data.handlers.RefereeViewModel
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.managers.RefereeManager

class LoginActivity : AppCompatActivity() {

    private val refereeService: RefereeViewModel by viewModels()

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
            .load("https://upload.wikimedia.org/wikipedia/commons/5/5a/FIFA_Referee.png") // TODO: Definir una URL para el logo
            .into(findViewById(R.id.logo))

        // campos de login
        val emailField = findViewById<EditText>(R.id.dniField)
        val passwordField = findViewById<EditText>(R.id.passwordField)

        // logica de login
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            login(emailField, passwordField)
        }

        // resultado de login:
        refereeService.referee.observe(this) { referee ->
            println(referee)
            if (referee == null) {
                emailField.error = "Invalid email"
                passwordField.error = "Invalid password"
                return@observe
            }

            // guarda el referee
            RefereeManager.setCurrentReferee(referee)
            // guarda el referee en localStorage
            LocalStorageService.saveRefereeReference(referee.id.toString(), referee.token)
            println(referee.token)
            // redirige a la pantalla de matches
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun login (emailField: EditText, passwordField: EditText) {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        // requiere los campos
        if (email.isBlank() || password.isBlank()) {
            emailField.error = "Email is required"
            passwordField.error = "Password is required"
            return
        }

        // valida credenciales
        val credentials = RefereeLogin(email, password)
        refereeService.login(credentials)

    }
}