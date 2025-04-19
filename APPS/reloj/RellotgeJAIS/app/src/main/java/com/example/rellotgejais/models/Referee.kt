package com.example.rellotgejais.models

data class Referee (
    val id: Int,
    val name: String,
    val dni: String,
    var password: String,
    var clock_code: String?
)

data class RefereeLogin (
    val dni: String,
    val password: String
)