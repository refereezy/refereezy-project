package com.example.refereezyapp.data.models


data class MatchReport (
    val id: String = "",
    val match_id: Int? = null,
    val referee_id: Int = 0,
    val done: Boolean = false,
    val timer: List<Int> = listOf(0,0)
) {
    lateinit var incidents: List<Incident>


    override fun toString(): String {
        var str = """
            Match Report:
            id: $id
            match_id: $match_id
            referee_id: $referee_id
            done: $done
            timer: ${timer[0]}:${timer[1]}
            incidents: ${incidents.size}
            """.trimIndent()

        for (incident in incidents) {
            str += " - ${incident}\n"
        }

        return str

    }
}

class PopulatedActa (
    val raw: MatchReport,
    val local: Team,
    val visiting: Team,
    val match: Match,
    val incidents: MutableList<PopulatedIncident> = mutableListOf<PopulatedIncident>()

)