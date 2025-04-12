package com.example.refereezyapp.data.models


data class MatchReport (
    val id: String = "",
    val match_id: Int? = null,
    val referee_id: Int = 0,
    var done: Boolean = false,
    val timer: MutableList<Int> = mutableListOf(0,0)
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

class PopulatedReport (
    val raw: MatchReport,
    val match: PopulatedMatch,
    val incidents: MutableList<PopulatedIncident> = mutableListOf()
)