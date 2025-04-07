package com.example.refereezyapp.data.handlers

import com.example.refereezyapp.data.FirebaseManager
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.PopulatedReport
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object ReportService {

    fun initReport(match: PopulatedMatch): PopulatedReport {
        val report = FirebaseManager.initReport(
            match.raw.id, match.raw.referee_id)

        val populated = PopulatedReport(report, match)

        return populated
    }

    fun updateReportTimer(reportId: String, newTimer: List<Int>): Boolean {
        var res = false
        runBlocking {
            res = async { FirebaseManager.updateReportTimer(reportId, newTimer) }.await()
        }
        return res
    }

    fun updateReportDone(reportId: String, done: Boolean = true): Boolean  {
        var res = false
        runBlocking {
            res = async { FirebaseManager.updateReportDone(reportId, done) }.await()
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

            val player = report.match.getPlayerById(incident.player_id)
            player?.team = report.match.getTeamById(player.team.id)!!
            val populated = PopulatedIncident(incident, player)

            report.incidents.add(populated)
            res = populated

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