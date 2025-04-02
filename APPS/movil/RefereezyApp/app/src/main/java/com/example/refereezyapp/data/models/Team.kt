package com.example.refereezyapp.data.models

data class Team (
    val id: Int,
    val name: String,
    val logo_url: String,
    val primary_color: String,
    val secondary_color: String,
    val players: List<Player> = emptyList()
) {

    override fun toString(): String {
        var str = """"
            Team -> $id: $name
            Primary Color: $primary_color
            Secondary Color: $secondary_color
        """.trimIndent()

        for (player in players) {
            str += "$player\n"
        }


        return str
    }
}
