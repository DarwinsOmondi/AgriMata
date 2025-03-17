package com.example.agrimata.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class FarmersAuthViewModel : ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    init {
        observeSession()
    }

    fun signUpFarmer(userName: String, userEmail: String, userPassword: String, userPhone: String?) {
        viewModelScope.launch {
            try {
                client.gotrue.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", JsonPrimitive(userName))
                        if (!userPhone.isNullOrBlank()) put("phone", JsonPrimitive(userPhone))
                    }
                }
                _userState.value = UserState.Success("User created successfully")
            } catch (e: Exception) {
                Log.e("SupabaseSignUp", "Error: ${e.message}")
                _userState.value = UserState.Error("Sign-up failed: ${e.localizedMessage}")
            }
        }
    }

    fun signInFarmer(email: String, password: String) {
        viewModelScope.launch {
            try {
                client.gotrue.loginWith(Email) {
                    this.email = email
                    this.password = password
                }
                _userState.value = UserState.Success("User logged in successfully")
            } catch (e: Exception) {
                Log.e("SupabaseSignIn", "Error: ${e.message}")
                _userState.value = UserState.Error("Sign-in failed: ${e.localizedMessage}")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                client.gotrue.logout()
                _userState.value = UserState.Success("User logged out successfully")
            } catch (e: Exception) {
                Log.e("SupabaseLogOut", "Error: ${e.message}")
                _userState.value = UserState.Error("Logout failed: ${e.localizedMessage}")
            }
        }
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            try {
                val user = client.gotrue.retrieveUserForCurrentSession()
                _userState.value = if (user != null) {
                    UserState.Success("User is logged in")
                } else {
                    UserState.Error("User is not logged in")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error checking session: ${e.localizedMessage}")
            }
        }
    }


    private fun observeSession() {
        viewModelScope.launch {
            client.gotrue.sessionStatus.collectLatest { session ->
                _userState.value = if (session != null) {
                    UserState.Success("User is logged in")
                } else {
                    UserState.Error("User is not logged in")
                }
            }
        }
    }
}
