package com.example.refereezyapp.data.models

data class Incident(
    val id: String = "",
    val description: String = "",
    val minute: Int = 0,
    val player_id: Int? = null,
    val type: IncidentType = IncidentType.OTHER
) {

    override fun toString(): String {
        return "Incident(id=$id, description='$description', minute=$minute, player_id=$player_id, type=$type)"
    }
}

class PopulatedIncident(
    val raw: Incident,
    val player: Player? = null
) {
    override fun toString(): String {
        return raw.toString()
    }
}


enum class IncidentType {
    GOAL, YELLOW_CARD, RED_CARD, LESION, FIGHT, OTHER;

    fun fromString(value: String): IncidentType {
        return when (value) {
            "GOAL" -> GOAL
            "YELLOW_CARD" -> YELLOW_CARD
            "RED_CARD" -> RED_CARD
            "LESION" -> LESION
            "FIGHT" -> FIGHT
            "OTHER" -> OTHER
            else -> throw Exception("Invalid incident type: $value")
        }
    }
}



