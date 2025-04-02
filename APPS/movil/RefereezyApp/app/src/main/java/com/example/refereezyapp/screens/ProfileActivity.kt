package com.example.refereezyapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.RefereeService
import com.example.refereezyapp.data.static.RefereeManager

class ProfileActivity : AppCompatActivity() {

    private val refereeService: RefereeService by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // components
        val userField = findViewById<TextView>(R.id.userField)
        val dniField = findViewById<TextView>(R.id.dniField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val clockCodeField = findViewById<EditText>(R.id.clockCodeField)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        // loading data

        val referee = RefereeManager.getCurrentReferee()!!

        userField.text = referee.name
        dniField.text = referee.dni
        passwordField.setText(referee.password)
        clockCodeField.setText(referee.clock_code)


        logoutBtn.setOnClickListener {
            refereeService.logout()
            finishAffinity()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }





    }
}