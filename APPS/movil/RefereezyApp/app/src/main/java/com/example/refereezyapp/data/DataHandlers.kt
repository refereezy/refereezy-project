package com.example.refereezyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.models.Clock
import com.example.refereezyapp.data.models.Incident
import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedIncident
import com.example.refereezyapp.data.models.PopulatedMatch
import com.example.refereezyapp.data.models.PopulatedReport
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.models.RefereeUpdate
import com.example.refereezyapp.data.models.Team
import com.example.refereezyapp.data.static.MatchManager
import com.example.refereezyapp.data.static.RefereeManager
import com.example.refereezyapp.data.static.ReportManager
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ConnectionService : ViewModel() {

    fun testConnection(): Boolean {
        var res = false

        runBlocking {
            try {
                val response = RetrofitManager.instance.testConnection()
                Log.d("Retrofit", "Conexion exitosa: $response")
                res = true
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }

        return res
    }
}

class RefereeService : ViewModel() {
    private val _referee = MutableLiveData<Referee>()
    val referee: LiveData<Referee> get() = _referee

    fun login(credentials: RefereeLogin): Referee? {

        var res: Referee? = null

        runBlocking {
            try {
                val response = async { RetrofitManager.instance.login(credentials) }.await()
                res = response
            } catch (e: Exception) {
                Log.e("Retrofit (login)", "Error de conexión: ${e.message}")
            }
        }

        return res
    }

    fun getReferee(id: Int, password: String): Referee? {

        var res: Referee? = null

        runBlocking {
            try {
                val response = async { RetrofitManager.instance.getReferee(id, password) }.await()
                res = response
            } catch (e: Exception) {
                Log.e("Retrofit (getReferee)", "Error de conexión: ${e.message}")
            }
        }

        return res
    }

    fun changePassword(referee: Referee, newPassword: String) {
        viewModelScope.launch {
            try {
                val update = RefereeUpdate(referee.password, newPassword)
                val response = RetrofitManager.instance.changePassword(referee.id, update)
                _referee.value = response
                RefereeManager.setCurrentReferee(response)
            } catch (e: Exception) {
                Log.e("Retrofit (changePassword)", "Error de conexión: ${e.message}")
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
                Log.e("Retrofit (pairClock)", "Error de conexión: ${e.message}")
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
                Log.e("Retrofit (revokeClock)", "Error de conexión: ${e.message}")
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

    fun loadMatches(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.getRefereeMatches(id)
                for (match in response) {
                    MatchManager.addMatch(match)
                }
                _matches.value = response
            } catch (e: Exception) {
                Log.e("Retrofit (loadMatches)", "Error de conexión: ${e.message}")
            }
        }
    }

    fun populateMatch(id: Int): PopulatedMatch? {
        var res: PopulatedMatch? = null
        runBlocking {
            try {
                val response = RetrofitManager.instance.getMatch(id)
                res = response
            } catch (e: Exception) {
                Log.e("Retrofit (populateMatch)", "Error de conexión: ${e.message}")

            }
        }

        return res
    }

    fun getTeam(id: Int): Team? {
        var res: Team? = null
        runBlocking {
            try {
                val response = async { RetrofitManager.instance.getTeam(id) }.await()
                res = response
            } catch (e: Exception) {
                Log.e("Retrofit (getTeam)", "Error de conexión: ${e.message}")
            }
        }

        return res
    }
}

object ReportService {

    fun initReport(match: PopulatedMatch): PopulatedReport {
        val report = FirebaseManager.initReport(
            match.raw.id, match.raw.referee_id)

        val populated = PopulatedReport(report, match)

        MatchManager.setCurrentMatch(match)
        ReportManager.setCurrentReport(populated)

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
