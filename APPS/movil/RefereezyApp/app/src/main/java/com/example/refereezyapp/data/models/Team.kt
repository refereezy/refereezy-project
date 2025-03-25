package com.example.refereezyapp.data.models

data class Team (
    val id: Int,
    val name: String,
    val logo: String,
    val primaryColor: String,
    val secondaryColor: String,
    val players: List<Player>
)
