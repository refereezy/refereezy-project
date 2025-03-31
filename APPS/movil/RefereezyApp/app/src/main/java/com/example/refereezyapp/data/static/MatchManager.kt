package com.example.refereezyapp.data.static

import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedMatch

object MatchManager {
    private val matches = mutableListOf<Match>()
    private var currentMatch: PopulatedMatch? = null

    fun addMatch(match: Match) {
        matches.add(match)
    }

    fun setCurrentMatch(match: PopulatedMatch) {
        currentMatch = match
    }

    fun clearCurrentMatch() {
        currentMatch = null
    }

    fun getCurrentMatch(): PopulatedMatch? {
        return currentMatch
    }

    fun getMatches(): List<Match> {
        return matches.toList()
    }

    fun removeMatch(matchId: Int) {
        matches.removeAll { it.id == matchId }
    }

    fun getMatchById(matchId: Int): Match? {
        return matches.find { it.id == matchId }
    }

    fun populateMatch(match: Match): PopulatedMatch {
        // rellena la lista de jugadores con los de los equipos por cada id
        val localTeam = TeamManager.getTeamById(match.local_team_id)
        val visitorTeam = TeamManager.getTeamById(match.visitor_team_id)

        val populated = PopulatedMatch()

    }

}