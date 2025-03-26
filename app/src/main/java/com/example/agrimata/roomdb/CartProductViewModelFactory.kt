package com.example.agrimata.roomdb

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agrimata.viewmodels.CartProductViewModel

class CartProductViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartProductViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
