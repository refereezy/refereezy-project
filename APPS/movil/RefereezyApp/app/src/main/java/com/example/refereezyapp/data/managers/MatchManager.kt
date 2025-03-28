package com.example.refereezyapp.data.managers

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

    fun updateMatch(match: Match) {
        val index = matches.indexOfFirst { it.id == match.id }
        if (index != -1) {
            matches[index] = match
        }
    }

    fun getMatchById(matchId: Int): Match? {
        return matches.find { it.id == matchId }
    }

    fun getMatchesForReferee(refereeId: Int): List<Match> {
        return matches.filter { it.referee_id == refereeId }
    }




}