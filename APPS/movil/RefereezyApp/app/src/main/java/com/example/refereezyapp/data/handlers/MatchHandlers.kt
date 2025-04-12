package com.example.refereezyapp.data.handlers

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.RetrofitManager
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.static.MatchManager
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@SuppressLint("StaticFieldLeak")
class MatchViewModel : ViewModel() {

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches

    private val _populatedMatch = MutableLiveData<PopulatedMatch>()
    val populatedMatch: LiveData<PopulatedMatch> get() = _populatedMatch

    fun loadMatches(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.getRefereeMatches(id)
                if (!response.isSuccessful) {
                    Log.e("Retrofit (loadMatches)", "Error de conexión: ${response.errorBody()}")
                    return@launch
                }

                val matches = response.body()!!
                MatchManager.clearMatches()

                for (match in matches) {
                    MatchManager.addMatch(match)
                }

                _matches.value = matches


            } catch (e: Exception) {
                Log.e("Retrofit (loadMatches)", "Error de conexión: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun populateMatch(id: Int) {
        viewModelScope.launch {
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
                    _populatedMatch.value = match
                }
            } catch (e: Exception) {
                Log.e("Retrofit (populateMatch)", "Error de conexión: ${e.message}")

            }
        }
    }

}

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