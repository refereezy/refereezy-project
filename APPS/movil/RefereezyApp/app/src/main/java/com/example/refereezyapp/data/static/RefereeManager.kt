package com.example.refereezyapp.data.static

import com.example.refereezyapp.data.models.Referee

object RefereeManager {

    private var currentReferee: Referee? = null

    fun getCurrentReferee(): Referee? {
        return currentReferee
    }

    fun setCurrentReferee(referee: Referee?) {
        currentReferee = referee
    }

    fun isLoggedIn(): Boolean {
        return currentReferee != null
    }

    fun logout() {
        currentReferee = null
    }

    fun getRefereeId(): Int? {
        return currentReferee?.id
    }

    fun getRefereeName(): String?{
        return currentReferee?.name
    }




}