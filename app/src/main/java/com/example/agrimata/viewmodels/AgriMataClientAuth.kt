package com.example.agrimata.viewmodels

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

class AgriMataClientAuth(): ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    init {
        observeSession()
    }

    fun SignUpUser(userName: String, userEmail: String, userPassword: String, userPhone: String?) {
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
                _userState.value = UserState.Success("User Created Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }


    fun SignInUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                client.gotrue.loginWith(Email) {
                    this.email = email
                    this.password = password
                }
                _userState.value = UserState.Success("User Signed In Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }
    fun LogOut() {
        viewModelScope.launch {
            try {
                client.gotrue.logout()
                _userState.value = UserState.Success("User Logged Out Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
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

//    fun ResetPassword(email: String,password: String) {
//        viewModelScope.launch {
//            try {
//                client.auth.resetPasswordForEmail(email)
//                client.auth.updateUser {
//                    this.password = password
//                }
//            } catch (e: Exception) {
//                _userState.value = UserState.Error(e.message.toString())
//            }
//        }
//    }
}