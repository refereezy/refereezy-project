package com.example.rellotgejais.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rellotgejais.data.managers.RefereeManager
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.data.services.RetrofitManager
import com.example.rellotgejais.models.Referee
import com.example.rellotgejais.models.RefereeLoad
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
    }

    fun getReferee(id: Int, token: String): Referee? {
        var res: Referee? = null

        runBlocking {
            try {
                val creds = RefereeLoad(id, token)
                val response = async { RetrofitManager.instance.loadReferee(creds) }.await()
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
