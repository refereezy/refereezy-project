package com.example.refereezyapp.data.services

import io.socket.client.IO

object SocketService {

    private val socket = IO.socket("http://10.0.2.2:3000")

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun emit(event: String, data: Any) {
        socket.emit(event, data)
    }

    fun on(event: String, listener: (Any) -> Unit) {
        socket.on(event) { args ->
            listener(args[0])
        }
    }



}