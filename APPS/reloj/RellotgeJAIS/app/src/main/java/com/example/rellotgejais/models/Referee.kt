package com.example.rellotgejais.models

data class Referee (
    val id: Int,
    val name: String,
    val dni: String,
    val token: String,
    var clock_code: String?
)

data class RefereeLoad (
    val id: Int,
    val token: String
)