package com.example.rellotgejais.data.handlers

import android.util.Log
import com.example.rellotgejais.data.services.RetrofitManager
import com.example.rellotgejais.models.PopulatedMatch


object MatchService {

    suspend fun getMatch(id: Int): PopulatedMatch? {
        try {
            val response = RetrofitManager.instance.getMatch(id)
            if (response.isSuccessful) {
                val match = response.body()

                if (match != null) {
                    for (player in match.visitor_team.players) {
                        player.team = match.visitor_team
                    }
                    for (player in match.local_team.players) {
                        player.team = match.local_team
                    }
                }

                return match
            }
            Log.e("Retrofit (populateMatch)", "Error de conexión: ${response.errorBody()}")
            return null
        } catch (e: Exception) {
            Log.e("Retrofit (populateMatch)", "Error de conexión: ${e.message}")
            return null
        }
    }
}