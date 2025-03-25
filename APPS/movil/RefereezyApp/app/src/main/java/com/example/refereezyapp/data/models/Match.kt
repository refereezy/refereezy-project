package com.example.refereezyapp.data.models

import com.google.type.DateTime

data class Match (
    val id: Int,
    val local: Team,
    val visiting: Team,
    val date: DateTime,
    val referee: Referee
) {

    fun isLocalPlayer(player: Player): Boolean {
        return player.team == local
    }

    fun isVisitantePlayer(player: Player): Boolean {
        return player.team == visiting
    }
}