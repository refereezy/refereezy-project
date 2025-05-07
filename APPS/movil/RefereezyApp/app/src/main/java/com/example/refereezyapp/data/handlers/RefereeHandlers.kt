package com.example.refereezyapp.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.services.LocalStorageService
import com.example.refereezyapp.data.services.RetrofitManager
import com.example.refereezyapp.data.models.Clock
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.models.RefereeUpdate
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.data.managers.ReportManager
import com.example.refereezyapp.data.models.RefereeLoad
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object RefereeService {

    fun login(referee: Referee) {
        RefereeManager.setCurrentReferee(referee)
        LocalStorageService.saveRefereeReference(referee.id.toString(), referee.token)
    }

    fun logout() {
        LocalStorageService.clearRefereeReference()
        RefereeManager.logout()
        ReportManager.setCurrentReport(null)
        MatchManager.clearMatches()
    }

}

class RefereeViewModel : ViewModel() {
    private val _referee = MutableLiveData<Referee>()
    val referee: LiveData<Referee> get() = _referee

    fun changePassword(referee: Referee, newPassword: String) {
        viewModelScope.launch {
            try {
                val update = RefereeUpdate(referee.token, newPassword)
                val response = RetrofitManager.instance.changePassword(referee.id, update)
                val value = response.body()
                _referee.value = value
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
                referee.clock_code = clockCode
                _referee.value = referee
            } catch (e: Exception) {
                Log.e("Retrofit (pairClock)", "Error de conexión", e)
            }
        }
    }

    fun revokeClock(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitManager.instance.revokeClock(id)
                val ref = RefereeManager.getCurrentReferee()
                ref?.clock_code = null
                _referee.value = ref
            } catch (e: Exception) {
                Log.e("Retrofit (revokeClock)", "Error de conexión: ${e.message}")
            }
        }
    }

    fun login(credentials: RefereeLogin) {
        viewModelScope.launch {
            try {
                val result = RetrofitManager.instance.login(credentials)
                if (result.isSuccessful) {
                    val ref = result.body()
                    _referee.value = ref
                } else {
                    Log.e("Retrofit (login)", "Error de conexión: ${result.errorBody()}")
                    _referee.value = null
                }
            } catch (e: Exception) {
                Log.e("Retrofit (login)", "Error de conexión: ${e.message}")
                _referee.value = null
            }
        }
    }

    fun getReferee(id: Int, token: String) {
        viewModelScope.launch {
            val creds = RefereeLoad(id, token)
            val response = RetrofitManager.instance.loadReferee(creds)
            if (response.isSuccessful) {
                val ref = response.body()
                _referee.value = ref
            } else {
                Log.e("Retrofit (getReferee)", "Error de conexión: ${response.errorBody()}")
                _referee.value = null
            }
        }
    }

}
