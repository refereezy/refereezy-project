package com.example.refereezyapp.data.services

import com.example.refereezyapp.data.models.RefereeLoad
import io.socket.client.IO

object SocketService {

    private val socket = IO.socket("http://10.0.2.2:3000")

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun pairCode(code: String, data: RefereeLoad) {
        socket.emit("validate-code", code, data)
    }

    fun notifyNewReport(id: String) {
        socket.emit("new-report", id)
    }



}