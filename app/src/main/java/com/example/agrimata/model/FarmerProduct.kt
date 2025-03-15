package com.example.agrimata.model

import kotlinx.serialization.Serializable

@Serializable
data class FarmerProduct(
    val productId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val pricePerUnit: Double = 0.0,
    val unit: String = "",
    val stockQuantity: Int = 0,
    val location: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
)