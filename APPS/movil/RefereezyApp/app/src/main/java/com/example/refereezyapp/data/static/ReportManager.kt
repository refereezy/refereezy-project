package com.example.refereezyapp.data.static

import com.example.refereezyapp.data.models.PopulatedReport

object ReportManager {

    private var currentReport: PopulatedReport? = null


    fun getCurrentReport(): PopulatedReport? {
        return currentReport
    }

    fun setCurrentReport(report: PopulatedReport) {
        currentReport = report
    }

    fun clearReport() {
        currentReport = null
    }


}

