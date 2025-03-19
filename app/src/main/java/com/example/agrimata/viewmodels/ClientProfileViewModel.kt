package com.example.agrimata.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.network.SuparBaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClientProfileViewModel : ViewModel() {
    private val _userProfileState = mutableStateOf<UserProfileState>(UserProfileState.Loading)
    val userProfileState:State<UserProfileState> = _userProfileState

    init {
        startUserProfileUpdates()
    }

    private fun startUserProfileUpdates() {
        viewModelScope.launch {
            while (true) {
                fetchUserProfile()
                delay(1000L)
            }
        }
    }
    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val user: UserInfo? = SuparBaseClient.client.gotrue.currentUserOrNull()
                if (user != null) {
                    _userProfileState.value = UserProfileState.Success(
                        name = ((user.userMetadata?.get("name")?: "Unknown").toString()),
                        email = user.email ?: "No Email",
                        phone = (user.userMetadata?.get("phone")?: "No Phone").toString(),
                        imageUrl = user.userMetadata?.get("imageUrl")?.toString() ?: ""
                    )
                } else {
                    _userProfileState.value = UserProfileState.Error("User not logged in")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error("Error fetching profile: ${e.message}")
                Log.e("UserProfileViewModel", "Error fetching user profile", e)
            }
        }
    }
}
