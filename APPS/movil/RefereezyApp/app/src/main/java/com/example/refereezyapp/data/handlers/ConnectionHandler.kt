package com.example.refereezyapp.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.services.RetrofitManager
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

object ConnectionService {

    fun testConnectionBloking(): Boolean {
        var res = false

        runBlocking {
            try {
                val response = async { RetrofitManager.instance.testConnection() }.await()

                if (!response.isSuccessful) {
                    Log.e("Retrofit", "Error de conexión: ${response.errorBody()}")
                    return@runBlocking
                }

                Log.d("Retrofit", "Conexion exitosa: ${response.body()}")
                res = true

            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
            }
        }

        return res
    }


}

class ConnectionViewModel : ViewModel() {

    private val _attemps = MutableLiveData<Int>()
    val attemps: LiveData<Int> = _attemps

    private val _connectionResult = MutableLiveData<Boolean>()
    val connectionResult: LiveData<Boolean> = _connectionResult

    fun testConnection() {
        viewModelScope.launch {
            try {
                val response = RetrofitManager.instance.testConnection()

                if (!response.isSuccessful) {
                    Log.e("Retrofit", "Error de conexión: ${response.errorBody()}")
                    _connectionResult.postValue(false)
                    return@launch
                }

                Log.d("Retrofit", "Conexion exitosa: ${response.body()}")
                _connectionResult.postValue(true)

            } catch (e: Exception) {
                Log.e("Retrofit", "Error de conexión: ${e.message}")
                _connectionResult.postValue(false)
            }
        }
    }

}