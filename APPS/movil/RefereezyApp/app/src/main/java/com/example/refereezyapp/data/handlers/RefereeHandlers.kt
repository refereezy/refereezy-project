package com.example.refereezyapp.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.LocalStorageManager
import com.example.refereezyapp.data.RetrofitManager
import com.example.refereezyapp.data.models.Clock
import com.example.refereezyapp.data.models.Referee
import com.example.refereezyapp.data.models.RefereeLogin
import com.example.refereezyapp.data.models.RefereeUpdate
import com.example.refereezyapp.data.managers.MatchManager
import com.example.refereezyapp.data.managers.RefereeManager
import com.example.refereezyapp.data.managers.ReportManager
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object RefereeService {

    fun login(referee: Referee) {
        RefereeManager.setCurrentReferee(referee)
        LocalStorageManager.saveRefereeReference(referee.id.toString(), referee.password)
    }

    fun logout() {
        LocalStorageManager.clearRefereeReference()
        RefereeManager.logout()
        ReportManager.setCurrentReport(null)
        MatchManager.clearMatches()
    }

    fun getReferee(id: Int, password: String): Referee? {
        var res: Referee? = null

        runBlocking {
            try {
                val response = async { RetrofitManager.instance.getReferee(id, password) }.await()
                if (response.isSuccessful) {
                    res = response.body()
                } else {
                    Log.e("Retrofit (getReferee)", "Error de conexión: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("Retrofit (getReferee)", "Error de conexión: ${e.message}")
            }
        }

        return res
    }

}

class RefereeViewModel : ViewModel() {
    private val _referee = MutableLiveData<Referee?>()
    val referee: LiveData<Referee?> get() = _referee

    fun changePassword(referee: Referee, newPassword: String) {
        viewModelScope.launch {
            try {
                val update = RefereeUpdate(referee.password, newPassword)
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
                Log.e("Retrofit (pairClock)", "Error de conexión: ${e.message}")
            }
        }
    }

    fun revokeClock(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitManager.instance.revokeClock(id)
                _referee.value = RefereeManager.getCurrentReferee()
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

    fun getReferee(id: Int, password: String) {
        viewModelScope.launch {
            val response = RetrofitManager.instance.getReferee(id, password)
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
