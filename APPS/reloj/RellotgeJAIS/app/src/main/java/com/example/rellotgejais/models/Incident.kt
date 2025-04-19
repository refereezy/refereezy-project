package com.example.rellotgejais.models

data class Incident(
    val id: String = "",
    val description: String = "",
    val minute: Int = 0,
    val player: PlayerIncident? = null,
    val type: IncidentType = IncidentType.OTHER
) {

    override fun toString(): String {
        return "Incident(id=$id, description='$description', minute=$minute, player_id=${player?.id}, type=$type)"
    }

    fun populateWith(match: PopulatedMatch): PopulatedIncident {
        val player = match.getPlayerById(player?.id)
        return PopulatedIncident(this, player)
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
    GOAL, YELLOW_CARD, RED_CARD, LESION, FIGHT, SUSPEND, OTHER
}
