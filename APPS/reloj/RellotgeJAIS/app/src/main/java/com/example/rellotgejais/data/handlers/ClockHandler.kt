package com.example.rellotgejais.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rellotgejais.data.services.LocalStorageService.saveClockQrCode
import com.example.rellotgejais.data.services.RetrofitManager
import com.example.rellotgejais.models.Clock
import kotlinx.coroutines.launch

class ClockHandler: ViewModel() {
    private val _clock = MutableLiveData<Clock?>()
    val clock: LiveData<Clock?> get() = _clock

    fun generateCode() {
        viewModelScope.launch {
            val response = RetrofitManager.instance.generateCode()
            if (response.isSuccessful) {
                val codeQr = response.body()
                _clock.value = codeQr
                saveClockQrCode(codeQr?.code!!)
            } else {
                Log.e("Retrofit (generateCode)", "Error de conexión: ${response.errorBody()}")
                _clock.value = null
            }
        }
    }
}