package com.example.agrimata.roomdb

import kotlinx.coroutines.flow.Flow

class CartProductRepository(private val cartProductDao: CartProductDao) {
    val allProducts: Flow<List<CartProduct>> = cartProductDao.getAllProducts()

    suspend fun insertProduct(product: CartProduct) {
        cartProductDao.insertProduct(product)
    }

    suspend fun deleteProduct(productId: String) {
        cartProductDao.deleteProduct(productId)
    }

    suspend fun deleteAllProducts() {
        cartProductDao.deleteAllProducts()
    }

    suspend fun updateProduct(product: CartProduct) {
        cartProductDao.updateProduct(product)
    }
}