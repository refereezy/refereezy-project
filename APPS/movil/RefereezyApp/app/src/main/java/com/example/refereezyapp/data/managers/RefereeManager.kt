package com.example.refereezyapp.data.managers

import com.example.refereezyapp.data.models.Referee

object RefereeManager {

    private var currentReferee: Referee? = null

    fun getCurrentReferee(): Referee? {
        return currentReferee
    }

    fun setCurrentReferee(referee: Referee) {
        currentReferee = referee
    }

    fun logout() {
        currentReferee = null
    }

}