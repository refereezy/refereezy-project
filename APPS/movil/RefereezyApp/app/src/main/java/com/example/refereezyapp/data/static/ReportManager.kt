package com.example.refereezyapp.data.static

import com.example.refereezyapp.data.models.MatchReport
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

    fun populateRecord(record: MatchReport): PopulatedReport {

        val match = MatchManager.getMatchById(record.match_id!!)

        if (match == null) {
            throw RuntimeException("Match not found with id: ${record.match_id}")
        }

        val populated = PopulatedReport(record, MatchManager.populateMatch(match))

        return  populated
        
    }

}

