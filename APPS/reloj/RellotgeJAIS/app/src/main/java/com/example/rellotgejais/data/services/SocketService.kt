package com.example.rellotgejais.data.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rellotgejais.Config
import com.example.rellotgejais.models.RefereeLoad
import com.google.gson.JsonSyntaxException
import io.socket.client.IO

object SocketService: ViewModel() {

    private var pairing = false

    private val _refereeLoad: MutableLiveData<RefereeLoad?> = MutableLiveData()
    val refereeLoad: LiveData<RefereeLoad?> get() = _refereeLoad

    private val _newReport: MutableLiveData<Int> = MutableLiveData(0)
    val newReport: LiveData<Int> get() = _newReport

    private val socket = IO.socket("http://${Config.API_URL}:3000")

    fun connect() {
        socket.connect()
        Log.d("SocketService", "Conectado al servidor socket")
    }

    fun disconnect() {
        socket.disconnect()
        Log.d("SocketService", "Desconectado del servidor socket")
    }

    fun registerCode(code: String) {
        socket.emit("register", code)
        Log.d("SocketService", "Código registrado: $code")
    }

    fun unregisterCode(code: String) {
        socket.emit("unregister", code)
        Log.d("SocketService", "Código desregistrado: $code")
    }

    fun awaitPairing() {
        if (pairing) return
        Log.d("SocketService", "Waiting for pairing")

        pairing = true
        socket.on("pair") { args ->
            val rawData = args[0].toString()
            try {
                val id = rawData.substringAfter("id=").substringBefore(",").toInt()
                val token = rawData.substringAfter("token=").substringBefore(")")

                val data = RefereeLoad(id, token)

                Log.d("SocketService", "Emparejando Referee ID: ${data.id}")

                _refereeLoad.postValue(data)
            } catch (e: JsonSyntaxException) {
                Log.e("SocketService", "Error al parsear el JSON: ${e.message}")
            }
        }

    }

    fun stopPairing() {
        pairing = false
        socket.off("pair")
        _refereeLoad.postValue(null)
        Log.d("SocketService", "Pareado detenido")
    }

    fun awaitReport() {
        _newReport.postValue(0)
        socket.on("new-report") { args ->
            Log.d("SocketService", "Nuevo reporte recibido, id: ${args[0]}")
            val currentValue = _newReport.value ?: 0
            _newReport.postValue(currentValue + 1)
        }
    }

}