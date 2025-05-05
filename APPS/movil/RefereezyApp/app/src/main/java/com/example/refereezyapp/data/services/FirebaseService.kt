package com.example.refereezyapp.data.services

import android.util.Log
import com.example.refereezyapp.data.handlers.MatchService
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.MatchReport
import com.example.refereezyapp.data.models.PopulatedReport
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseService {

    private  val REPORT_COLLECTION = "reports"
    private  val INCIDENT_COLLECTION = "incidents"

    private val db by lazy { Firebase.firestore }


    suspend fun getReport(refereeId: Int): PopulatedReport? {

        val reportQuery = db.collection(REPORT_COLLECTION)
            .whereEqualTo("referee_id", refereeId) // actas de arbitro
            .whereEqualTo("done", false) // solo actas sin acabar
            .limit(1) // to not saturate the result

        var result: QuerySnapshot? = null

        result = reportQuery.get().await()

        if (result.isEmpty == true) { // no results
            Log.e("Firebase", "Report not found")
            return null
        }

        val reportRef = result.documents[0] // first

        // maps the data to Report object, and adds the firebase_id to it
        val matchReport = reportRef.toObject(MatchReport::class.java)!!.copy(id = reportRef.id)

        // get incidents
        val incidents = getIncidents(reportRef)
        matchReport.incidents = incidents // sets the incidents

        val populatedMatch = MatchService.getMatch(matchReport.match_id!!)

        if (populatedMatch == null) {
            Log.e("Firebase", "Match not found with id: ${matchReport.match_id} for report: ${matchReport.id}")
            return null
        }

        val populatedInicents = incidents.map { incident ->incident.populateWith(populatedMatch) }.toMutableList()

        val populated = PopulatedReport(matchReport, populatedMatch, populatedInicents)

        return populated
    }

    fun getReport(refereeId: Int, callback: (PopulatedReport?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val rep = getReport(refereeId)
            withContext(Dispatchers.Main) { callback(rep) }
        }
    }

    private suspend fun getIncidents(reportRef: DocumentSnapshot): List<Incident> {
        val incidentRef = reportRef.reference.collection(INCIDENT_COLLECTION) // gets the incidencts subcollection
        val resultIncidencias = incidentRef.get().await()

        return resultIncidencias.documents.map { doc ->
            // maps the incident and adds the firebase_id
            doc.toObject(Incident::class.java)!!.copy(id = doc.id)
        }
    }

    suspend fun initReport(matchId: Int, refereeId: Int): MatchReport {
        val db = Firebase.firestore

        val reportRef = db.collection(REPORT_COLLECTION).document()

        val report = MatchReport(
            id = reportRef.id,
            match_id = matchId,
            referee_id = refereeId
        )

        reportRef.set(report).await()

        return report
    }

    suspend fun updateReportTimer(reportId: String, newTimer: List<Int>): Boolean {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        if (newTimer.size != 2) {
            Log.e("Firebase", "Invalid timer format")
            return false
        }

        return try {
            reportRef.update("timer", newTimer).await() // Wait for update to complete
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating timer: ${e.message}")
            false
        }
    }

    suspend fun updateReportDone(reportId: String, done: Boolean): Boolean {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        return try {
            reportRef.update("done", done).await() // Wait for update to complete
            true
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating done: ${e.message}")
            false
        }
    }

    suspend fun addIncident(reportId: String, incident: Incident): Incident? {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId) // get the report doc ref

        val res = reportRef.get().await()

        if (!res.exists()) {
            Log.e("Firebase", "Report not found or does not exist")
            return null
        }

        val incidentRef = reportRef.collection(INCIDENT_COLLECTION).document()
        val updated = incident.copy(id = incidentRef.id)

        incidentRef.set(updated).await()

        return updated

    }

    suspend fun removeIncident(reportId: String, incidentId: String): Boolean {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        val res = reportRef.get().await()


        if (!res.exists()) {
            Log.e("Firebase", "Report not found or does not exist")
            return false
        }

        val incidentRef = reportRef.collection(INCIDENT_COLLECTION).document(incidentId)

        incidentRef.delete().await()

        return true

    }

    suspend fun getDoneMatches(refereeId: Int): List<Long> {
        val reportQuery = db.collection(REPORT_COLLECTION)
            .whereEqualTo("referee_id", refereeId)
            .whereEqualTo("done", true)

        val result = reportQuery.get().await()

        val ids = mutableListOf<Long>()

        result.documents.forEach {
            ids.add(it.get("match_id") as Long)
        }

        return ids
    }

}