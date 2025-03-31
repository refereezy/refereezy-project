package com.example.refereezyapp.data

import android.util.Log
import com.example.refereezyapp.data.models.MatchReport
import com.example.refereezyapp.data.models.Incident
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object FirebaseManager {

    private const val REPORT_COLLECTION = "reports"
    private const val INCIDENT_COLLECTION = "incidents"

    private val db by lazy { Firebase.firestore }


    fun getReport(refereeId: Int, dataHandler: (result: MatchReport?, errorMessage: String?) -> Unit) {

        db.collection(REPORT_COLLECTION)
            .whereEqualTo("referee_id", refereeId) // actas de arbitro
            .whereEqualTo("done", false) // solo actas sin acabar
            .limit(1) // to not saturate the result
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) { // no results
                    dataHandler(null, "Report not found")
                    return@addOnSuccessListener
                }

                val reportRef = result.documents[0] // first

                // maps the data to Report object, and adds the firebase_id to it
                val matchReport = reportRef.toObject(MatchReport::class.java)!!.copy(id = reportRef.id)

                val incidentRef = reportRef.reference.collection(INCIDENT_COLLECTION) // gets the incidencts subcollection

                incidentRef.get()
                    .addOnSuccessListener { resultIncidencias ->
                        val incidents = mutableListOf<Incident>() // list of incidencias
                        for (doc in resultIncidencias.documents) { // for each result
                            // maps the incidencia and adds the firebase_id
                            val incident = doc.toObject(Incident::class.java)!!.copy(id = doc.id)

                            incidents.add(incident) // adds incidenct
                        }
                        matchReport.incidents = incidents // sets the incidencts

                        dataHandler(matchReport, null)
                    }

                    .addOnFailureListener { e ->
                        val message = "Error getting incidencias: $e"
                        Log.e("Firebase", message)
                        dataHandler(null, message) // returns null to indicate error
                    }

            }

            .addOnFailureListener { e ->
                val message = "Error getting acta: $e"
                Log.e("Firebase", message)
                dataHandler(null, message) // Gives an error message
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

    fun updateReportTimer(reportId: String, newTimer: List<Int>) {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        if (newTimer.size != 2) {
            Log.e("Firebase", "Invalid timer format")
            return
        }

        reportRef.update("timer", newTimer)
            .addOnFailureListener { e ->
                val message = "Error updating timer: $e"
                Log.e("Firebase", message)
            }
    }

    fun updateReportDone(reportId: String, done: Boolean) {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        reportRef.update("done", done)
    }

    fun addIncident(reportId: String, incident: Incident, responseHandler: (res: Incident) -> Unit) {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId) // get the report doc ref

        reportRef.get().addOnCompleteListener { task ->
            if (!task.isSuccessful || !task.result.exists()) {
                Log.e("Firebase", "Report not found or does not exist")
                return@addOnCompleteListener
            }

            val incidentRef = reportRef.collection(INCIDENT_COLLECTION).document()
            val updated = incident.copy(id = incidentRef.id)
            incidentRef.set(updated)
                .addOnSuccessListener {
                    responseHandler(updated)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error adding incident: $e")
                }
        }

    }

    fun removeIncident(reportId: String, incidentId: String, onSuccess: () -> Unit) {
        val reportRef = db.collection(REPORT_COLLECTION).document(reportId)

        reportRef.get().addOnCompleteListener { task ->
            if (!task.isSuccessful || !task.result.exists()) {
                Log.e("Firebase", "Report not found or does not exist")
                return@addOnCompleteListener
            }

            val incidentRef = reportRef.collection(INCIDENT_COLLECTION).document(incidentId)

            incidentRef.delete().addOnSuccessListener {
                onSuccess()
            }
        }
    }

}