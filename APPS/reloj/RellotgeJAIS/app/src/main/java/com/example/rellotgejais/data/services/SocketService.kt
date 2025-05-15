package com.example.rellotgejais.data.services

import android.util.Log
import com.example.rellotgejais.Config
import io.socket.client.IO
import io.socket.client.Socket

object SocketService {

    private val _socket = IO.socket("http://${Config.API_URL}:3000")
    val socket: Socket get() = _socket

    fun connect() {
        _socket.connect()
        Log.d("SocketService", "Conectado al servidor socket")
    }

    fun disconnect() {
        _socket.disconnect()
    }

    fun isConnected(): Boolean {
        return _socket.connected()
    }

    fun notifyTimerChange(id: String, min: Int, sec: Int) {
        _socket.emit("timer-updated", id, min, sec)
    }

    init {
        _socket.on("disconnect") {
            Log.d("SocketService", "Desconectado del servidor socket")
        }
    }

}