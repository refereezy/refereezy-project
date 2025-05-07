package com.example.refereezyapp.data.services

import com.example.refereezyapp.data.models.RefereeLoad
import io.socket.client.IO
import io.socket.client.Socket

object SocketService {

    private val _socket = IO.socket("http://10.0.2.2:3000")
    val socket: Socket get() = _socket

    fun connect() {
        _socket.connect()
    }

    fun disconnect() {
        _socket.disconnect()
    }

    fun pairCode(code: String, data: RefereeLoad) {
        _socket.emit("validate-code", code, data)
    }

    fun notifyNewReport(id: String) {
        _socket.emit("new-report", id)
    }



}