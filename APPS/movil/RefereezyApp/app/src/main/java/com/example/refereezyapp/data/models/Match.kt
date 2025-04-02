package com.example.refereezyapp.data.models

import java.time.LocalDateTime

data class Match (
    val id: Int,
    val local_team_id: Int,
    val visitor_team_id: Int,
    val date: LocalDateTime,
    val referee_id: Int
) {
    override fun toString(): String {
        return "Match(id=$id, local_team_id=$local_team_id, visitor_team_id=$visitor_team_id, date=$date, referee_id=$referee_id)"
    }
}

data class PopulatedMatch (
    val raw: Match,
    val local_team: Team,
    val visitor_team: Team,
    val referee: Referee
) {

    fun isLocalPlayer(player: Player): Boolean {
        return player.team == local_team
    }

    fun isVisitantPlayer(player: Player): Boolean {
        return player.team == visitor_team
    }

    fun getPlayerById(id: Int?): Player? {
        for (player in local_team.players.plus(visitor_team.players)) {
            if (player.id == id) {
                return player
            }
        }

        return null
    }

    override fun toString(): String {
        return "PopulatedMatch(id=${raw.id}, local_team=${local_team.name}, visitor_team=${visitor_team.name}, date=${raw.date}, referee=${referee.dni})"
    }
}