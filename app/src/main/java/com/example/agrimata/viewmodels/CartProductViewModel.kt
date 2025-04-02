package com.example.agrimata.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.roomdb.CartProduct
import com.example.agrimata.roomdb.CartProductDatabase
import com.example.agrimata.roomdb.CartProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CartProductViewModel(application: Application) : AndroidViewModel(application) {
    private val cartProductRepository: CartProductRepository

    init {
        val database = CartProductDatabase.getDatabase(application)
        cartProductRepository = CartProductRepository(database.cartProductDao)
    }

    val allProducts: Flow<List<CartProduct>> = cartProductRepository.allProducts

    fun insertProduct(product: CartProduct) = viewModelScope.launch {
        cartProductRepository.insertProduct(product)
    }

    fun deleteProduct(productId: String) = viewModelScope.launch {
        cartProductRepository.deleteProduct(productId)
    }

    fun deleteAllProducts() = viewModelScope.launch {
        cartProductRepository.deleteAllProducts()
    }

    fun updateProduct(product: CartProduct) = viewModelScope.launch {
        cartProductRepository.updateProduct(product)
    }
}