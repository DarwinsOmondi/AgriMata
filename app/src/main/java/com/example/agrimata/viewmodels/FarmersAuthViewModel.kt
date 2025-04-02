package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.Farmer
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.util.UUID

class FarmersAuthViewModel() : ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    private val _isFarmer = mutableStateOf(false)
    val isFarmer: State<Boolean> = _isFarmer

    private val _profileImage = mutableStateOf<ByteArray?>(null)
    val profileImage: State<ByteArray?> = _profileImage

    private val _farmers = MutableStateFlow<List<Farmer>>(emptyList())
    val farmers: StateFlow<List<Farmer>> = _farmers

    private val _farmerUri = mutableStateOf<ByteArray?>(null)
    val farmerUri: State<ByteArray?> = _farmerUri

    init {
        observeSession()
        checkIfUserIsFarmer()
        fetchClientImage()
    }

    fun signUpFarmer(
        userName: String,
        userEmail: String,
        userPassword: String,
        userPhone: String?,
        role: String?
    ) {
        viewModelScope.launch {
            try {
                client.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", JsonPrimitive(userName))
                        if (!userPhone.isNullOrBlank()) put("phone", JsonPrimitive(userPhone.toLong()))
                        put("role", JsonPrimitive(role))
                        if (!role.isNullOrBlank()) put("role", JsonPrimitive(role))
                    }
                }
                _userState.value = UserState.Success("Framer Account created successfully")
            } catch (e: Exception) {
                Log.e("SupabaseSignUp", "Error: ${e.message}")
                _userState.value = UserState.Error("Sign-up failed: ${e.localizedMessage}")
            }
        }
    }

    fun signInFarmer(email: String, password: String) {
        viewModelScope.launch {
            try {
                client.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                    this.email = email
                    this.password = password
                }
                _userState.value = UserState.Success("Framer Account logged in successfully")
            } catch (e: Exception) {
                Log.e("SupabaseSignIn", "Error: ${e.message}")
                _userState.value = UserState.Error("Sign-in failed: ${e.localizedMessage}")
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                client.auth.signOut()
                _userState.value = UserState.Success("User logged out successfully")
            } catch (e: Exception) {
                Log.e("SupabaseLogOut", "Error: ${e.message}")
                _userState.value = UserState.Error("Logout failed: ${e.localizedMessage}")
            }
        }
    }

    fun checkIfUserIsFarmer() {
        viewModelScope.launch {
            try {
                val user = client.auth.currentSessionOrNull()?.user
                val role = user?.userMetadata?.get("role")?.jsonPrimitive?.content
                if (role == "farmer") {
                    _isFarmer.value = true
                    _userState.value = UserState.Success("User is a Farmer")
                } else {
                    _isFarmer.value = false
                    _userState.value = UserState.Error("User is not a Farmer")
                }
            } catch (e: Exception) {
                _userState.value =
                    UserState.Error("Error checking farmer role: ${e.localizedMessage}")
            }
        }
    }


    fun resetPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                client.auth.resetPasswordForEmail(email)
                client.auth.updateUser {
                    this.password = password
                }
                _userState.value = UserState.Success("Password reset email sent successfully")
            } catch (e: Exception) {
                Log.e("SupabaseResetPassword", "Error: ${e.message}")
                _userState.value = UserState.Error("Reset password failed: ${e.localizedMessage}")
            }
        }
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            try {
                val user = client.auth.currentSessionOrNull()?.user
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

    fun addFarmerToListOfFarmers(farmer: Farmer) {
        viewModelScope.launch {
            try {
                client.postgrest["farmers"].insert(farmer)
            } catch (e: Exception) {
                Log.e("SupabaseAddFarmer", "Error: ${e.message}")
            }
        }
    }


    private fun observeSession() {
        viewModelScope.launch {
            client.auth.sessionStatus.collectLatest { session ->
                _userState.value = if (session != null) {
                    UserState.Success("User is logged in")
                } else {
                    UserState.Error("User is not logged in")
                }
            }
        }
    }

    fun updateFarmerDetail(name: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                client.auth.updateUser {
                    data = buildJsonObject {
                        put("name", JsonPrimitive(name))
                        put("email", JsonPrimitive(email))
                        put("phone", JsonPrimitive(phone.toInt()))
                    }
                }
                _userState.value = UserState.Success("User details updated successfully")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Update failed: ${e.localizedMessage}")
            }
        }
    }

    fun createFramerProfileBucket() {
        viewModelScope.launch {
            try {
                val user = client.auth.currentSessionOrNull()?.user
                val bucketName = user?.userMetadata?.get("email")?.jsonPrimitive?.content
                    ?: throw Exception("User email not found")

                val existingBuckets = client.storage.retrieveBuckets()
                if (existingBuckets.any { it.id == bucketName }) {
                    _userState.value = UserState.Success("Bucket '$bucketName' already exists")
                    return@launch
                } else {
                    client.storage.createBucket(id = bucketName) {
                        public = false
                        fileSizeLimit = 10.megabytes
                    }
                    _userState.value =
                        UserState.Success("Bucket '$bucketName' created successfully")
                }

            } catch (e: Exception) {
                _userState.value = UserState.Error("Bucket creation failed: ${e.message}")
            }
        }
    }

    fun uploadFarmerImageToSupabase(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val user = client.auth.currentSessionOrNull()?.user
                val bucketName = user?.userMetadata?.get("email")?.jsonPrimitive?.content
                    ?: throw Exception("User email not found")
                val imageFileName = "${UUID.randomUUID()}.jpg"
                val bucket = client.storage[bucketName]

                val imageByteArray =
                    context.contentResolver.openInputStream(imageUri)?.use { (it.readBytes()) }
                        ?: throw IOException("Failed to read image file")

                try {
                    bucket.delete(imageFileName)
                } catch (_: Exception) {
                }

                client.auth.updateUser {
                    data = buildJsonObject {
                        put("profile_image", JsonPrimitive(imageFileName))
                    }
                }
                _userState.value = UserState.Success("Image uploaded successfully to bucket")

            } catch (e: Exception) {
                _userState.value = UserState.Error("Image upload failed: ${e.message}")
            }
        }
    }

    fun fetchClientImage() {
        viewModelScope.launch {
            try {
                val user = client.auth.currentSessionOrNull()?.user
                val bucketName = user?.userMetadata?.get("email")?.jsonPrimitive?.content
                    ?: throw Exception("User email not found")
                val imageFileName = user.userMetadata?.get("profile_image")?.jsonPrimitive?.content

                if (!imageFileName.isNullOrBlank()) {
                    val bucket = client.storage[bucketName]

                    val byteArray = bucket.downloadAuthenticated(imageFileName)
                    _profileImage.value = byteArray
                } else {
                    _userState.value = UserState.Error("No profile image found in metadata")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Failed to fetch image: ${e.message}")
            }
        }
    }
}
