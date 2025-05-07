package com.example.refereezyapp.screens.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.refereezyapp.R
import com.example.refereezyapp.data.handlers.RefereeService
import com.example.refereezyapp.data.handlers.RefereeViewModel
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.utils.PopUp

class ProfileActivity : AppCompatActivity() {

    private val refereeViewModel: RefereeViewModel by viewModels()

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
        val matchBtn = findViewById<ImageButton>(R.id.matchBtn)
        val editPasswordBtn = findViewById<ImageButton>(R.id.editPasswordBtn)
        val editClockBtn = findViewById<ImageButton>(R.id.editClockBtn)

        // loading data
        val referee = RefereeManager.getCurrentReferee()!!

        userField.text = referee.name
        dniField.text = referee.dni
        clockCodeField.setText(
            if (referee.clock_code != null) referee.clock_code!!.substring(0, 10)+"..." else "None"
        )


        // behaviour
        logoutBtn.setOnClickListener {
            RefereeService.logout()
            finishAffinity()
            openActivity(LoginActivity::class.java)
        }

        matchBtn.setOnClickListener {
            openActivity(MatchActivity::class.java, true)
        }

        editPasswordBtn.setOnClickListener {
            passwordField.isEnabled = !passwordField.isEnabled

            if (passwordField.isEnabled) {
                passwordField.requestFocus()
                passwordField.error = "Can't be empty"
            }
            else {
                passwordField.setText("")
                passwordField.clearFocus()
            }
        }

        passwordField.setOnEditorActionListener { _, _, _ ->

            if (!isValidPassword(passwordField.text.toString())) {
                passwordField.error = "At least 8 character"
                passwordField.requestFocus()
                return@setOnEditorActionListener false
            }

            refereeViewModel.changePassword(referee, passwordField.text.toString())

            passwordField.isEnabled = false
            passwordField.setText("")
            passwordField.clearFocus()

            return@setOnEditorActionListener true
        }

        editClockBtn.setOnClickListener {
            refereeViewModel.revokeClock(referee.id)
            openActivity(PairingClockActivity::class.java)
        }

        // changes on referee
        refereeViewModel.referee.observe(this) {
            if (it != null) {
                RefereeService.login(it)
                return@observe
            }

            PopUp.Companion.show(this, "Something went wrong", PopUp.Type.ERROR)
        }

        refereeViewModel.getReferee(referee.id, referee.token)

    }

    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 8
    }

    fun openActivity(activity: Class<*>, end: Boolean = false) {
        val intent = Intent(this, activity)
        startActivity(intent)
        if (end) finish()
    }
}