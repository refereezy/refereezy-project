package com.example.refereezyapp.data.handlers

import android.util.Log
import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.PopulatedReport
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object ReportService {

    suspend fun initReport(match: PopulatedMatch): PopulatedReport {
        val report = FirebaseManager.initReport(
            match.raw.id, match.raw.referee_id)

        val populated = PopulatedReport(report, match)

        return populated
    }

    suspend fun updateReportTimer(report: PopulatedReport, newTimer: List<Int>): Boolean {
        Log.d("ReportService", "Updating timer: $newTimer")
        var res = FirebaseManager.updateReportTimer(report.raw.id, newTimer)
        if (!res) {
            Log.e("ReportService", "Error updating timer")
        } else {
            report.raw.timer[0] = newTimer[0]
            report.raw.timer[1] = newTimer[1]
        }
        return res
    }

    suspend fun updateReportTimer(report: PopulatedReport, totalSeconds: Int): Boolean {
        val newTimer = listOf(totalSeconds / 60, totalSeconds % 60)
        return updateReportTimer(report, newTimer)
    }

    fun endReport(report: PopulatedReport, done: Boolean = true): Boolean  {
        var res = false
        runBlocking {
            res = async { FirebaseManager.updateReportDone(report.raw.id, done) }.await()
            report.raw.done = true
        }
        return res
    }

    fun addIncident(report: PopulatedReport, incident: Incident): PopulatedIncident? {

        var res: PopulatedIncident? = null

        runBlocking {
            val incident = async { FirebaseManager.addIncident(report.raw.id, incident) }.await()

            if (incident == null) {
                return@runBlocking
            }

            val player = report.match.getPlayerById(incident.player?.id)
            // commented this line cz it looks redundant
            // player?.team = report.match.getTeamById(player.team.id)!!
            val populated = PopulatedIncident(incident, player)

            report.incidents.add(populated)
            res = populated
            println("Incident added: $incident")

        }

        return res

    }

    fun removeIncident(report: PopulatedReport, incident: Incident): Boolean {

        var success = false
        runBlocking {
            success = async { FirebaseManager.removeIncident(report.raw.id, incident.id) }.await()
            if (success) {
                report.incidents.removeIf { it.raw.id == incident.id }
            }
        }

        return success
    }

}