package com.example.agrimata.model

data class CooperativeGroup(
    val groupId: String = "",
    val groupName: String = "",
    val adminId: String = "",
    val members: List<String> = emptyList(),
    val groupProducts: List<String> = emptyList(),
    val totalRevenue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
