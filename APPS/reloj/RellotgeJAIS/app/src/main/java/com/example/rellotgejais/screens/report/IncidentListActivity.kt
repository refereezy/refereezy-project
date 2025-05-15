package com.example.rellotgejais.screens.report

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rellotgejais.R
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.utils.adapters.IncidentAdapter


class IncidentListActivity : _BaseReportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_list)

        //! this class extends BaseReportActivity, which initializes the basic values

        // components
        val incidentRecycler = findViewById<RecyclerView>(R.id.incidentRecycler)

        // drawing data
        incidentRecycler.layoutManager = LinearLayoutManager(this)
        incidentRecycler.adapter = IncidentAdapter(report.incidents
            .sortedBy { it.raw.minute }
            .toMutableList(), report)


    }

}