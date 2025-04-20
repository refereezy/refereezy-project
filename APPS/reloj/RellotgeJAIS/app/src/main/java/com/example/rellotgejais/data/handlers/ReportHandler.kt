package com.example.rellotgejais.data.handlers


import android.util.Log
import com.example.rellotgejais.data.services.FirebaseService
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.models.PopulatedIncident
import com.example.rellotgejais.models.PopulatedReport
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object ReportHandler {

    suspend fun getReport(refereeId: Int): PopulatedReport? {
        return FirebaseService.getReport(refereeId)
    }


    suspend fun updateReportTimer(report: PopulatedReport, newTimer: List<Int>): Boolean {
        Log.d("ReportService", "Updating timer: $newTimer")
        var res = FirebaseService.updateReportTimer(report.raw.id, newTimer)
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
            res = async { FirebaseService.updateReportDone(report.raw.id, done) }.await()
            report.raw.done = true
        }
        return res
    }

    fun addIncident(report: PopulatedReport, incident: Incident): PopulatedIncident? {

        var res: PopulatedIncident? = null

        runBlocking {
            val incident = async { FirebaseService.addIncident(report.raw.id, incident) }.await()

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
            success = async { FirebaseService.removeIncident(report.raw.id, incident.id) }.await()
            if (success) {
                report.incidents.removeIf { it.raw.id == incident.id }
            }
        }

        return success
    }

}