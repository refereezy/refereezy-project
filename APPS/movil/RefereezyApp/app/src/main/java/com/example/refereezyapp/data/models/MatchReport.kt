package com.example.refereezyapp.data.models


data class MatchReport (
    val id: String = "",
    val matchId: Int = 0,
    val refereeId: Int = 0,
    val done: Boolean = false,
    val timer: List<Int> = listOf(0,0)
) {

    lateinit var incidents: List<Incident>
}

class PopulatedActa (
    val raw: MatchReport,
    val local: Team,
    val visiting: Team,
    val match: Match,
    val incidents: MutableList<PopulatedIncident> = mutableListOf<PopulatedIncident>()

)