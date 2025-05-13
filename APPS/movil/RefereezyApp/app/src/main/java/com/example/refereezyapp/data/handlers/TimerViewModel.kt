package com.example.refereezyapp.data.handlers

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refereezyapp.data.managers.ReportManager
import kotlinx.coroutines.launch

class TimerViewModel: ViewModel() {
    // guarda el tiempo que ha pasado (en segundos).
    private val _elapsedTime = MutableLiveData<Int>(0)
    val elapsedTime: LiveData<Int> = _elapsedTime

    // timer para conteo y otro timer para guardado
    private var timer: CountDownTimer? = null
    private var storingTimer: CountDownTimer? = null

    private val TIMER_UPDATE_INTERVAL: Long = 1_000
    private val TIMER_STORE_INTERVAL: Long = 30_000

    private var startTime: Long = 0
    var isRunning = false
    var isStarted = false

    fun initTimer(minutes: Int = 0, seconds: Int = 0) {
        // Calcula el tiempo total en segundos
        val totalSeconds = minutes * 60 + seconds
        println("Timer Init: totalSeconds: $totalSeconds")

        // Establece ese tiempo como el tiempo transcurrido
        _elapsedTime.value = totalSeconds
    }

    fun initStartingPoint() {
        if (isStarted || isRunning) return

        startTime = System.currentTimeMillis() - (_elapsedTime.value?: 0) * 1000
        isStarted = true // do not remove 💀
    }

    fun play() {
        if (!isStarted) initStartingPoint()
        if (isRunning) return

        //Aquí empieza el temporizador. Cada segundo (1000ms), actualiza elapsedTime.
        timer = object : CountDownTimer(Long.MAX_VALUE, TIMER_UPDATE_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                //Calcula el startTime para poder reanudar desde donde se quedó si estaba pausado.
                val seconds = _elapsedTime.value!! + 1
                _elapsedTime.postValue(seconds)

            }

            override fun onFinish() {}
        }.start()

        storingTimer = object : CountDownTimer(Long.MAX_VALUE, TIMER_STORE_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val report = ReportManager.getCurrentReport()!!
                viewModelScope.launch {
                    ReportService.updateReportTimer(report, _elapsedTime.value!!)
                }
            }

            override fun onFinish() {}
        }.start()

        isRunning=true
    }

    fun getElapsedMinutes(): Int {
        return _elapsedTime.value!! / 60
    }

    fun stop() {
        //Detiene el cronómetro sin borrar el tiempo.
        timer?.cancel()
        storingTimer?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        //Detiene el cronómetro y pone el tiempo en 0.
        stop()
        _elapsedTime.value = 0
        isStarted = false
    }

    fun setCustomTime(minutes: Int, seconds: Int) {
        stop()
        _elapsedTime.value = minutes * 60 + seconds
    }

    override fun onCleared() {
        //Se llama automáticamente si el ViewModel se destruye. Aquí se limpia la memoria.
        super.onCleared()
        timer?.cancel()
        storingTimer?.cancel()
    }

}