package com.example.agrimata.model

sealed class UserProfileState {
    object Loading : UserProfileState()
    data class Success(
        val name: String,
        val email: String,
        val phone: String,
        val imageUrl: String
    ) : UserProfileState()

    data class Error(val message: String) : UserProfileState()
}