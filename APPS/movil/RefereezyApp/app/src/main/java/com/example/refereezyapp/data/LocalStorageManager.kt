package com.example.refereezyapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object LocalStorageManager {

    private const val PREFS_NAME = "RefereezyPrefs"
    private const val KEY_REFEREE_ID = "refereeId"
    private const val KEY_REFEREE_PASS = "refereePass"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveRefereeReference(refereeId: String, refereePass: String) {
        sharedPreferences.edit(commit = true) { putString(KEY_REFEREE_ID, refereeId) }
        sharedPreferences.edit(commit = true) { putString(KEY_REFEREE_PASS, refereePass) }
    }

    fun clearRefereeReference() {
        sharedPreferences.edit(commit = true) { remove(KEY_REFEREE_ID) }
        sharedPreferences.edit(commit = true) { remove(KEY_REFEREE_PASS) }
    }

    fun getRefereeId(): String? {
        return sharedPreferences.getString(KEY_REFEREE_ID, null)
    }

    fun getRefereePass(): String? {
        return sharedPreferences.getString(KEY_REFEREE_PASS, null)
    }
}