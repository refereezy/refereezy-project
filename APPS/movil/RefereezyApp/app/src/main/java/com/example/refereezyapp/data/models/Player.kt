package com.example.refereezyapp.data.models

data class Player (
    val id: Int,
    val nombre: String,
    val dorsal: Int
) {
    lateinit var team: Team
}