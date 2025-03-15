package com.example.agrimata.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AgriMataFireBaseAuth(): ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState
    val auth = FirebaseAuth.getInstance()

    fun SignUpUser(userName: String, userEmail: String, userPassword: String, userPhone: String?) {
        viewModelScope.launch {
            try {
                //create User in Firebase Authentication
                auth.createUserWithEmailAndPassword(userEmail, userPassword).await()
                    .user?.updateProfile(
                    userProfileChangeRequest {
                        displayName = userName
                    }
                )?.await()
                //Update UI with Success
                _userState.value = UserState.Success("User Created Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }


    fun SignInUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                //Sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password).await()
                //Update UI state to success
                _userState.value = UserState.Success("User Signed In Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }
    fun LogOut() {
        viewModelScope.launch {
            try {
                //Sign out from Firebase
                auth.signOut()
                //Update UI state to success
                _userState.value = UserState.Success("User Logged Out Successfully")

            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }

}