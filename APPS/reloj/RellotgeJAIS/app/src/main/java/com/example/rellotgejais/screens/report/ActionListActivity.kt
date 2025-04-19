package com.example.rellotgejais.screens.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rellotgejais.R
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.screens.IncidenciaAdapter


class ActionListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IncidenciaAdapter
    private val listaIncidencias = mutableListOf<Incident>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        recyclerView.setHasFixedSize(true)

        // Agregar datos de ejemplo
       /* listaIncidencias.apply {
            add(Incident(R.drawable.gol, 4, "Gol", 12, 0, "Gol"))
            add(Incident(R.drawable.t_amarilla, 10, "Tarjeta Amarilla", 14, 0, "T_Amarilla"))
            add(Incident(R.drawable.gol, 4, "Gol", 12, 0, "Gol"))
            add(Incident(R.drawable.t_amarilla, 10, "Tarjeta Amarilla", 14, 0, "T_Amarilla"))
        }

        adapter = IncidenciaAdapter(listaIncidencias)
        recyclerView.adapter = adapter
    */
    }

}