package com.example.rellotgejais.data.managers

import com.example.rellotgejais.models.Referee

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