package com.example.refereezyapp.data.managers

import com.example.refereezyapp.data.models.MatchReport

object ReportManager {

    private var currentReport: MatchReport? = null
    private val reportHistory = mutableListOf<MatchReport>()


    fun getCurrentReport(): MatchReport? {
        return currentReport
    }

    fun setCurrentReport(report: MatchReport) {
        currentReport = report
        addReportToHistory(report)
    }

    private fun addReportToHistory(report: MatchReport) {
        reportHistory.add(report)
    }

    fun clearReport(report: MatchReport) {
        if (currentReport == report) currentReport = null
        reportHistory.remove(report)
    }

    fun getReportHistory(): List<MatchReport> {
        return reportHistory.toList()
    }

    fun getReportById(reportId: String): MatchReport? {
        return reportHistory.find { it.id == reportId }
    }


}

