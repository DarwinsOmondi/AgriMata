package com.example.agrimata.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: String,
    val name: String,
    val description: String,
    val category: String,
    val pricePerUnit: Double,
    val unit: String,
    val stockQuantity: Int,
    val location: String,
    val imageUrl: String,
)