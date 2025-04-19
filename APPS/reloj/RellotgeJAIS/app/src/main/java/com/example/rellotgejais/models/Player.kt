package com.example.rellotgejais.models

data class Player (
    val id: Int,
    val name: String,
    val dorsal: Int,
    val goalkeeper: Boolean
) {
    lateinit var team: Team

    override fun toString(): String {
        return "Player -> $id: $name ($dorsal)"
    }

    fun toPlayerIncident(match: PopulatedMatch): PlayerIncident {
        return PlayerIncident(
            id = id,
            name = name,
            dorsal = dorsal,
            goalkeeper = this@Player.goalkeeper,
            team_local = match.isLocalPlayer(this)
        )
    }
}

data class PlayerIncident (
    val id: Int? = null,
    val name: String = "",
    val dorsal: Int? = null,
    val goalkeeper: Boolean = false,
    val team_local: Boolean = true
)