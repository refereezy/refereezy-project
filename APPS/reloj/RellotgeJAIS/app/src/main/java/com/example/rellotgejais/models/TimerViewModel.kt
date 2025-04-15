package com.example.rellotgejais.models

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel: ViewModel() {
    // guarda el tiempo que ha pasado (en segundos).
    private val elapsedTime = MutableLiveData<Long>(0)

    //es la versión pública para que otros fragmentos puedan "observar" los cambios sin modificarlo directamente.
    val liveElapsedTime: LiveData<Long> = elapsedTime

    //timer: el temporizador real, startTime: cuándo empezó y isRunning: si ya está corriendo o no.
    private var timer: CountDownTimer? = null
    private var startTime: Long = 0
    var isRunning = false

    fun startTimer() {
        if (isRunning) return
        //Aquí empieza el temporizador. Cada segundo (1000ms), actualiza _elapsedTime.
        startTime = System.currentTimeMillis() - (elapsedTime.value ?: 0) * 1000

        //Aquí empieza el temporizador. Cada segundo (1000ms), actualiza _elapsedTime.
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //Calcula el startTime para poder reanudar desde donde se quedó si estaba pausado.
                val seconds = (System.currentTimeMillis() - startTime) / 1000
                elapsedTime.postValue(seconds)

            }

            override fun onFinish() {}
        }.start()
        isRunning=true
    }
    fun stopTimer() {
        //Detiene el cronómetro sin borrar el tiempo.
        timer?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        //Detiene el cronómetro y pone el tiempo en 0.
        stopTimer()
        elapsedTime.value = 0
        isRunning = false
    }
    override fun onCleared() {
        //Se llama automáticamente si el ViewModel se destruye. Aquí se limpia la memoria.
        super.onCleared()
        timer?.cancel()
    }
    fun setCustomTime(minutes: Int, seconds: Int) {
        stopTimer()

        // Calcula el tiempo total en segundos
        val totalSeconds = (minutes * 60 + seconds).toLong()

        // Establece ese tiempo como el tiempo transcurrido
        elapsedTime.value = totalSeconds

        // Ajusta el startTime como si se hubiera iniciado en el pasado
        startTime = System.currentTimeMillis() - totalSeconds * 1000
    }




}