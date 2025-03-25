package com.example.refereezyapp.data.models

data class Referee (
    val id: Int,
    val nombre: String,
    val dni: String,
    var password: String,
    var clockCode: String?
)