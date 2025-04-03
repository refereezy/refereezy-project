package com.example.refereezyapp.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.example.refereezyapp.data.models.MatchReport
import com.example.refereezyapp.data.models.Incident
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private const val REPORT_COLLECTION = "reports"
    private const val INCIDENT_COLLECTION = "incidents"

    private val db by lazy { Firebase.firestore }


    fun getReport(refereeId: Int): MatchReport? {

        val reportQuery = db.collection(REPORT_COLLECTION)
            .whereEqualTo("referee_id", refereeId) // actas de arbitro
            .whereEqualTo("done", false) // solo actas sin acabar
            .limit(1) // to not saturate the result

        var result: QuerySnapshot? = null

        runBlocking {
            result = reportQuery.get().await()
        }

        if (result?.isEmpty == true) { // no results
            Log.e("Firebase", "Report not found")
            return null
        }

        val reportRef = result!!.documents[0] // first

        // maps the data to Report object, and adds the firebase_id to it
        val matchReport = reportRef.toObject(MatchReport::class.java)!!.copy(id = reportRef.id)

        // get incidents
        runBlocking {
            val incidents = async { getIncidents(reportRef) }.await()
            matchReport.incidents = incidents // sets the incidents
        }

        return matchReport
    }

    private suspend fun getIncidents(reportRef: DocumentSnapshot): List<Incident> {
        val incidentRef = reportRef.reference.collection(INCIDENT_COLLECTION) // gets the incidencts subcollection
        val resultIncidencias = incidentRef.get().await()

        return resultIncidencias.documents.map { doc ->
            // maps the incident and adds the firebase_id
            doc.toObject(Incident::class.java)!!.copy(id = doc.id)
        }
    }

    fun initReport(matchId: Int, refereeId: Int): MatchReport {
        val db = Firebase.firestore

        val reportRef = db.collection(REPORT_COLLECTION).document()
        val report = MatchReport(
            id = reportRef.id,
            match_id = matchId,
            referee_id = refereeId)

        reportRef.set(report)

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

}