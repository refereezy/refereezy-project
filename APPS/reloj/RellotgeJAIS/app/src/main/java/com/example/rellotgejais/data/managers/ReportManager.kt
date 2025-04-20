package com.example.rellotgejais.data.managers

import com.example.rellotgejais.models.PopulatedReport

object ReportManager {

    private var currentReport: PopulatedReport? = null


    fun getCurrentReport(): PopulatedReport? {
        return currentReport
    }

    fun setCurrentReport(report: PopulatedReport?) {
        currentReport = report
    }

    fun clearReport() {
        currentReport = null
    }


}

