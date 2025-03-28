package com.example.refereezyapp.data.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.RetrofitManager
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.managers.RefereeManager
import kotlinx.coroutines.launch

class SystemViewModel : ViewModel() {

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

class RefereeViewModel : ViewModel() {
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

    fun changePassword(id: Int, password: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val update = RefereeUpdate(password, newPassword)
                val response = RetrofitManager.instance.changePassword(id, update)
                _referee.value = response
                RefereeManager.setCurrentReferee(response)
            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }
    }

    fun pairClock(id: Int, clockCode: String) {
        viewModelScope.launch {
            try {
                val clock = Clock(clockCode)
                RetrofitManager.instance.assignClock(id, clock)
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

class MatchViewModel : ViewModel() {
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
