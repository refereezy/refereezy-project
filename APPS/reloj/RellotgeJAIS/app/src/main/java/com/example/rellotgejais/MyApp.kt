package com.example.rellotgejais

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.rellotgejais.models.TimerViewModel

class MyApp : Application(), ViewModelStoreOwner {

    private val appViewModelStore = ViewModelStore()
    val timerViewModel: TimerViewModel by lazy {
        TimerViewModel()
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

