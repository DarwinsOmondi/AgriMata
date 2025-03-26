package com.example.agrimata.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartProductDao {
    @Query("SELECT * FROM cart_items")
    fun getAllProducts(): Flow<List<CartProduct>>

    @Insert
    suspend fun insertProduct(product: CartProduct)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteProduct(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAllProducts()

    @Update
    suspend fun updateProduct(product: CartProduct)
}