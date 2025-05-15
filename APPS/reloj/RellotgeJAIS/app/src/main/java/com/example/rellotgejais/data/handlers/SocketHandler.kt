package com.example.rellotgejais.data.handlers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rellotgejais.data.managers.ReportManager
import com.example.rellotgejais.data.services.LocalStorageService
import com.example.rellotgejais.data.services.SocketService
import com.example.rellotgejais.models.Incident
import com.example.rellotgejais.models.RefereeLoad
import com.google.gson.Gson

class SocketHandler: ViewModel() {

    companion object {
        private val gson = Gson()
    }

    private val socket = SocketService.socket

    private var pairing = false

    private val _refereeLoad: MutableLiveData<RefereeLoad?> = MutableLiveData()
    val refereeLoad: LiveData<RefereeLoad?> get() = _refereeLoad

    private val _newReport: MutableLiveData<Int> = MutableLiveData(0)
    val newReport: LiveData<Int> get() = _newReport



    fun registerCode(code: String) {
        socket.emit("register", code)
        Log.d("SocketService", "Código registrado: $code")
    }

    fun unregisterCode(code: String) {
        socket.emit("unregister", code)
        Log.d("SocketService", "Código desregistrado: $code")
    }

    fun receivedReport(code: String, reportId: String) {
        socket.emit("report-received", code, reportId)
    }

    fun unlinkReport(code: String, reportId: String) {
        socket.emit("report-done", code, reportId)
        ReportManager.setCurrentReport(null)
    }

    fun awaitPairing() {
        if (pairing) return
        Log.d("SocketService", "Waiting for pairing")

        pairing = true
        socket.on("pair") { args ->
            try {
                val data = gson.fromJson(args[0].toString(), RefereeLoad::class.java)

                Log.d("SocketService", "Emparejando Referee ID: ${data.id}")

                _refereeLoad.postValue(data)
            } catch (e: Exception) {
                Log.e("SocketService", "Error al parsear el JSON: ${args[0]}",)
            }
        }

    }

    fun confirmPair(code: String) {
        socket.emit("pair-confirmed", code)
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
            val qr = LocalStorageService.getClockQrCode()!!
            receivedReport(qr, args[0].toString())
            val currentValue = _newReport.value ?: 0
            _newReport.postValue(currentValue + 1)
        }
    }

    fun notifyNewIncident(reportId: String, incidentId: String) {
        socket.emit("new-incident", reportId, incidentId)
    }

}