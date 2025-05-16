package com.example.refereezyapp.screens.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.refereezyapp.R
import com.example.refereezyapp.data.services.FirebaseService.getDoneReports
import com.example.refereezyapp.utils.PDFUtils
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope // Importar para usar lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.Team
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ActaActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acta)

        val container = findViewById<LinearLayout>(R.id.laterMatches)
        val referee = RefereeManager.getCurrentReferee()!!



        drawerLayout = findViewById(R.id.drawer_layout)
        menuBtn = findViewById(R.id.menuBtn)
        setupDrawer()
        // Llamar a la función suspendida dentro de una corrutina




        lifecycleScope.launch {
            getDoneReports(referee.id) { reports -> // pon aquí el ID real
                // Ejecutar en el hilo principal (UI) para modificar la interfaz
                runOnUiThread {
                    reports.forEach { report ->
                        val itemView = LayoutInflater.from(this@ActaActivity)
                            .inflate(R.layout.layout_endedmatch_info, container, false)

                        // Aquí puedes setear logos, textos, etc. si quieres
                        inflateMatchInfo(report.match, itemView)

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
    fun loadTeamLogo(team: Team, imageView: ImageView) {
        Glide.with(this)
            .load(team.logo_url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
            .onLoadFailed(ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null))
    }
    fun inflateMatchInfo(match: PopulatedMatch, matchInfo: View) {

        val localLogo = matchInfo.findViewById<ImageView>(R.id.localLogo)
        val visitorLogo = matchInfo.findViewById<ImageView>(R.id.visitorLogo)

        val localTeam = match.local_team
        val visitorTeam = match.visitor_team

        loadTeamLogo(localTeam, localLogo)
        loadTeamLogo(visitorTeam, visitorLogo)


    }

    private fun setupDrawer() {
        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    // Si ya estoy en HomeActivity, no la cargo de nuevo
                    val intent = Intent(this, MatchActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
                    // Si ya estoy en SettingsActivity, no la cargo de nuevo
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)

                }
                R.id.nav_certificates -> {
                    Toast.makeText(this, "Certificados", Toast.LENGTH_SHORT).show()
                    // Si ya estoy en ActaActivity, no la cargo de nuevo
                    if (this !is ActaActivity) {
                        val intent = Intent(this, ActaActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

}
