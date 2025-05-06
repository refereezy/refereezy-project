package com.example.rellotgejais.data.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rellotgejais.models.RefereeLoad
import io.socket.client.IO

object SocketService: ViewModel() {

    private var pairing = false

    private val _refereeLoad: MutableLiveData<RefereeLoad> = MutableLiveData()
    val refereeLoad: LiveData<RefereeLoad> get() = _refereeLoad

    private val _newReport: MutableLiveData<Int> = MutableLiveData(0)
    val newReport: LiveData<Int> get() = _newReport

    private val socket = IO.socket("http://10.0.2.2:3000")

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

        pairing = true
        socket.on("pair") { args ->
            val data = args[0] as RefereeLoad
            Log.d("SocketService", "Emparejando Referee ID: ${data.id}")
            _refereeLoad.value = data
        }
    }

    fun stopPairing() {
        pairing = false
        socket.off("pair")
        Log.d("SocketService", "Pareado detenido")
    }

    fun awaitReport() {
        socket.on("new-report") { args ->
            Log.d("SocketService", "Nuevo reporte recibido, id: ${args[0]}")
            _newReport.value = _newReport.value!! + 1
        }
    }

}