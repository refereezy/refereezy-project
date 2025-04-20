package com.example.refereezyapp.data.managers

import com.example.refereezyapp.data.models.Match
import com.example.refereezyapp.data.models.PopulatedMatch

object MatchManager {
    private val matches = mutableListOf<Match>()

    fun addMatch(match: Match) {
        matches.add(match)
    }

    fun getMatches(): List<Match> {
        return matches.toList()
    }

    fun clearMatches() {
        matches.clear()
    }

    fun removeMatch(matchId: Int) {
        matches.removeAll { it.id == matchId }
    }

    fun getMatchById(matchId: Int): Match? {
        return matches.find { it.id == matchId }
    }

}