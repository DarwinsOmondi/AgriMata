package com.example.agrimata.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.network.SuparBaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class ProfileViewModel : ViewModel() {
    private val _userProfileState = mutableStateOf<UserProfileState>(UserProfileState.Loading)
    val userProfileState: State<UserProfileState> = _userProfileState

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
                val user = SuparBaseClient.client.auth.currentUserOrNull()
                if (user != null) {
                    val role = user.userMetadata?.get("role")?.jsonPrimitive?.content
                    when (role) {
                        "client" -> {
                            // Fetch client-specific data
                            _userProfileState.value = UserProfileState.Success(
                                name = (user.userMetadata?.get("name") ?: "Unknown").toString(),
                                email = user.email ?: "No Email",
                                phone = (user.userMetadata?.get("phone") ?: "No Phone").toString(),
                                imageUrl = user.userMetadata?.get("imageUrl")?.toString() ?: ""
                            )
                        }

                        "farmer" -> {
                            // Fetch farmer-specific data
                            _userProfileState.value = UserProfileState.Success(
                                name = (user.userMetadata?.get("name") ?: "Unknown").toString(),
                                email = user.email ?: "No Email",
                                phone = (user.userMetadata?.get("phone") ?: "No Phone").toString(),
                                imageUrl = user.userMetadata?.get("profile_image")?.toString() ?: ""
                            )
                        }

                        else -> {
                            _userProfileState.value = UserProfileState.Error("Unknown user role")
                        }
                    }
                } else {
                    _userProfileState.value = UserProfileState.Error("User not logged in")
                }
            } catch (e: Exception) {
                _userProfileState.value =
                    UserProfileState.Error("Error fetching profile: ${e.message}")
                Log.e("UserProfileViewModel", "Error fetching user profile", e)
            }
        }
    }
}
