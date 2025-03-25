package com.example.refereezyapp.data.models

import java.time.LocalDateTime


data class Match (
    val id: Int,
    val local_team: Team,
    val visitor_team: Team,
    val date: LocalDateTime,
    val referee: Referee
) {

    fun isLocalPlayer(player: Player): Boolean {
        return player.team == local_team
    }

    fun isVisitantPlayer(player: Player): Boolean {
        return player.team == visitor_team
    }
}