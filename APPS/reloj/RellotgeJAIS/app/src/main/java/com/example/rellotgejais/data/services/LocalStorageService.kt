package com.example.rellotgejais.data.services

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object LocalStorageService {

    private const val PREFS_NAME = "RefereezyPrefs"
    private const val KEY_REFEREE_ID = "refereeId"
    private const val KEY_REFEREE_TOKEN = "refereeToken"
    private const val KEY_CLOCK_QR_CODE = "clockQrCode"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveRefereeReference(refereeId: String, token: String) {
        sharedPreferences.edit(commit = true) { putString(KEY_REFEREE_ID, refereeId) }
        sharedPreferences.edit(commit = true) { putString(KEY_REFEREE_TOKEN, token) }
    }

    fun clearRefereeReference() {
        sharedPreferences.edit(commit = true) { remove(KEY_REFEREE_ID) }
        sharedPreferences.edit(commit = true) { remove(KEY_REFEREE_TOKEN) }
    }

    fun getRefereeId(): String? {
        return sharedPreferences.getString(KEY_REFEREE_ID, null)
    }

    fun getRefereeToken(): String? {
        return sharedPreferences.getString(KEY_REFEREE_TOKEN, null)
    }
    fun saveClockQrCode(qrCode: String) {
        sharedPreferences.edit(commit = true) { putString(KEY_CLOCK_QR_CODE, qrCode) }
    }
}