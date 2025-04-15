package com.example.refereezyapp


import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.refereezyapp.data.handlers.TimerViewModel

class MyApp : Application(), ViewModelStoreOwner {

    private val appViewModelStore = ViewModelStore()
    val timerViewModel: TimerViewModel by lazy {
        TimerViewModel()
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}