package com.example.refereezyapp.data.handlers

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    // guarda el tiempo que ha pasado (en segundos).
    private val elapsedTime = MutableLiveData<Int>(0)

    //es la versión pública para que otros fragmentos puedan "observar" los cambios sin modificarlo directamente.
    val liveElapsedTime: LiveData<Int> = elapsedTime

    //timer: el temporizador real, startTime: cuándo empezó y isRunning: si ya está corriendo o no.
    private var timer: CountDownTimer? = null
    private var startTime: Long = 0
    var isRunning = false
    var isStarted = false

    fun initTimer(minutes: Int = 0, seconds: Int = 0) {
        // Calcula el tiempo total en segundos
        val totalSeconds = minutes * 60 + seconds
        println("INIT: totalSeconds: $totalSeconds")

        // Establece ese tiempo como el tiempo transcurrido
        elapsedTime.value = totalSeconds
    }

    fun initStartingPoint() {
        if (isStarted || isRunning) return

        startTime = System.currentTimeMillis() - (elapsedTime.value?: 0) * 1000
        isStarted = true // do not remove 💀
    }

    fun play() {
        if (!isStarted) initStartingPoint()
        if (isRunning) return

        //Aquí empieza el temporizador. Cada segundo (1000ms), actualiza elapsedTime.
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //Calcula el startTime para poder reanudar desde donde se quedó si estaba pausado.
                val seconds = (System.currentTimeMillis() - startTime) / 1000
                elapsedTime.postValue(seconds.toInt())

            }

            override fun onFinish() {}
        }.start()

        isRunning=true
    }
    fun stop() {
        //Detiene el cronómetro sin borrar el tiempo.
        timer?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        //Detiene el cronómetro y pone el tiempo en 0.
        stop()
        elapsedTime.value = 0
        isRunning = false
        isStarted = false
    }
    override fun onCleared() {
        //Se llama automáticamente si el ViewModel se destruye. Aquí se limpia la memoria.
        super.onCleared()
        timer?.cancel()
    }

}