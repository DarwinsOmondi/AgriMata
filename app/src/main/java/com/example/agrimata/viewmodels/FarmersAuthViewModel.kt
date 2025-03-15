package com.example.agrimata.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class FarmersAuthViewModel: ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    fun SignUpFarmer(userName: String, userEmail: String, userPassword: String, userPhone: String?) {
        viewModelScope.launch {
            try {
                SuparBaseClient.client.gotrue.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", JsonPrimitive(userName))
                        if (!userPhone.isNullOrBlank()) put(
                            "phone",
                            JsonPrimitive(userPhone)
                        )
                    }
                }
            } catch (supabaseError: Exception) {
                Log.e("SuparbaseSignUp", "Error: ${supabaseError.message}")
            }
        }
    }

    fun SignInFarmer(email: String, password: String) {
        viewModelScope.launch {
            try {
                SuparBaseClient.client.gotrue.loginWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (supabaseError: Exception) {
                Log.e("SupabaseSignIn", "Error: ${supabaseError.message}")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                SuparBaseClient.client.gotrue.logout()
            } catch (supabaseError: Exception) {
                Log.e("SupabaseLogOut", "Error: ${supabaseError.message}")
            }
        }
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            try {
                val session = SuparBaseClient.client.gotrue.currentSessionOrNull()
                _userState.value = if (session != null) {
                    UserState.Success("User is logged in")
                } else {
                    UserState.Error("User is not logged in")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }
}