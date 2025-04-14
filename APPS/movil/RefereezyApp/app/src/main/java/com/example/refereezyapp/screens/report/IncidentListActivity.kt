package com.example.refereezyapp.screens.report

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.refereezyapp.R
import com.example.refereezyapp.utils.recyclers.IncidentAdapter

class IncidentListActivity : _BaseReportActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incident_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val incidentRecycler = findViewById<RecyclerView>(R.id.incidentRecycler)
        val flipBtn = findViewById<ImageButton>(R.id.flipBtn)

        // drawing data
        incidentRecycler.layoutManager = LinearLayoutManager(this)
        incidentRecycler.adapter = IncidentAdapter(report.incidents
            .sortedBy { it.raw.minute }
            .toMutableList(), report)



        // behaviours
        flipBtn.setOnClickListener(this::flipScreen)

    }



}