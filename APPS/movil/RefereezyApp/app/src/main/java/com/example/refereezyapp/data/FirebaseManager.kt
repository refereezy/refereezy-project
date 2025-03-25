package com.example.refereezyapp.data

import android.util.Log
import com.example.refereezyapp.data.models.MatchReport
import com.example.refereezyapp.data.models.Incident
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object FirebaseManager {

    private val db by lazy { Firebase.firestore }


    fun getReport(idArbitro: Int, dataHandler: (result: MatchReport?, errorMessage: String?) -> Unit) {

        db.collection("reports")
            .whereEqualTo("referee_id", idArbitro) // actas de arbitro
            .whereEqualTo("done", false) // solo actas sin acabar
            .limit(1) // to not saturate the result
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) { // no results
                    dataHandler(null, "Report not found")
                    return@addOnSuccessListener
                }

                val actaRef = result.documents[0] // first

                // maps the data to Report object, and adds the firebase_id to it
                val matchReport = actaRef.toObject(MatchReport::class.java)!!.copy(id = actaRef.id)

                val incidenciasRef = actaRef.reference.collection("incidents") // gets the incidencts subcollection

                incidenciasRef.get()
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


}