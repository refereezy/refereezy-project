package com.example.refereezyapp.data.models

data class Player (
    val id: Int,
    val name: String,
    val dorsal: Int,
    val is_goalkeeper: Boolean
) {
    lateinit var team: Team

    override fun toString(): String {
        return "Player -> $id: $name ($dorsal)"
    }
}