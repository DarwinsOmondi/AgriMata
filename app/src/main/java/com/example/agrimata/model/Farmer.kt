package com.example.agrimata.model

import kotlinx.serialization.Serializable


@Serializable
data class Farmer(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
)
