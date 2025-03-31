package com.example.refereezyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.models.Clock
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.MatchReport
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.models.RefereeUpdate
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import com.example.refereezyapp.data.static.ReportManager
import kotlinx.coroutines.launch

class ConnectionService : ViewModel() {

    fun testConnection() {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.testConnection()
                Log.d("Retrofit", "Conexion exitosa: $response")
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }
}

class RefereeService : ViewModel() {
    private val _referee = MutableLiveData<Referee>()
    val referee: LiveData<Referee> get() = _referee

    fun login(dni: String, password: String) {
        viewModelScope.launch {
            try {
                val credentials = RefereeLogin(dni, password)
                val response = RetrofitManager.instance.login(credentials)
                _referee.value = response
                RefereeManager.setCurrentReferee(response)
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun changePassword(referee: Referee, newPassword: String) {
        viewModelScope.launch {
            try {
                val update = RefereeUpdate(referee.password, newPassword)
                val response = RetrofitManager.instance.changePassword(referee.id, update)
                _referee.value = response
                RefereeManager.setCurrentReferee(response)
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun pairClock(referee: Referee, clockCode: String) {
        viewModelScope.launch {
            try {
                val clock = Clock(clockCode)
                RetrofitManager.instance.assignClock(referee.id, clock)
                RefereeManager.getCurrentReferee()?.clockCode = clockCode
                _referee.value = RefereeManager.getCurrentReferee()

            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun revokeClock(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitManager.instance.revokeClock(id)
                RefereeManager.getCurrentReferee()?.clockCode = null
                _referee.value = RefereeManager.getCurrentReferee()
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun logout() {
        RefereeManager.logout()
        _referee.value = null
    }
}

class MatchService : ViewModel() {
    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches

    private val _populatedMatch = MutableLiveData<PopulatedMatch>()
    val populatedMatch: LiveData<PopulatedMatch> get() = _populatedMatch

    fun getMatches(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.getRefereeMatches(id)
                for (match in response) {
                    MatchManager.addMatch(match)
                }
                _matches.value = response
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun populateMatch(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.getMatch(id)
                MatchManager.setCurrentMatch(response)
                _populatedMatch.value = response
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }
}

object ReportService {

    fun initReport(match: PopulatedMatch) {
        val report = FirebaseManager.initReport(
            match.raw.id, match.raw.referee_id)

        val populated = PopulatedReport(report, match)
        ReportManager.setCurrentReport(populated)
    }

    fun updateReportTimer(reportId: String, newTimer: List<Int>) {
        FirebaseManager.updateReportTimer(reportId, newTimer)
    }

    fun updateReportDone(reportId: String, done: Boolean = true) {
        FirebaseManager.updateReportDone(reportId, done)
    }

    fun addIncident(report: PopulatedReport, incident: Incident) {

        FirebaseManager.addIncident(report.raw.id, incident) { updated ->
            val player = report.match.getPlayerById(incident.player_id)
            report.incidents.add(PopulatedIncident(updated, player))
        }

    }

    fun removeIncident(report: PopulatedReport, incident: Incident) {
        FirebaseManager.removeIncident(report.raw.id, incident.id) {
            report.incidents.removeIf { it.raw.id == incident.id }
        }
    }


}
