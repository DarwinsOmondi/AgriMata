package com.example.agrimata.model

data class Grant(
    val grantId: String = "",
    val title: String = "",
    val description: String = "",
    val eligibility: String = "",
    val amount: Double = 0.0,
    val deadline: Long = 0L,
    val status: String = "open",
    val applicants: List<String> = emptyList()
)
