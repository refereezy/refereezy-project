package com.example.refereezyapp.data.services

import com.example.refereezyapp.Config
import com.example.refereezyapp.data.models.RefereeLoad
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket

object SocketService {

    private val _socket = IO.socket("http://${Config.API_DOMAIN}:3000")
    val socket: Socket get() = _socket
    private val gson = Gson()

    fun connect() {
        _socket.connect()
    }

    fun disconnect() {
        _socket.disconnect()
    }

    fun pairCode(code: String, data: RefereeLoad) {
        _socket.emit("pair-code", code, gson.toJson(data))
    }

    fun notifyNewReport(id: String, code: String? = null) {
        _socket.emit("new-report", id, code)
    }

    fun notifyReportChange(id: String) {
        _socket.emit("report-updated", id)
    }

    fun notifyTimerChange(id: String, min: Int, sec: Int) {
        _socket.emit("timer-updated", id, min, sec)
    }





}