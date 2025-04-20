package com.example.refereezyapp.data.models

data class Referee (
    val id: Int,
    val name: String,
    val dni: String,
    val token: String,
    var clock_code: String?
)

data class RefereeLogin (
    val dni: String,
    val password: String
)

data class RefereeUpdate (
    val token: String,
    val new_password: String
)

data class RefereeLoad (
    val id: Int,
    val token: String
)