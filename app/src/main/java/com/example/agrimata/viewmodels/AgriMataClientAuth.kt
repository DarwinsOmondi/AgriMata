package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.util.UUID

class AgriMataClientAuth: ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    private val _profileImage = mutableStateOf<ByteArray?>(null)
    val profileImage: State<ByteArray?> = _profileImage

    init {
        observeSession()
        fetchClientImage()
    }


    fun SignUpUser(userName: String, userEmail: String, userPassword: String, userPhone: String?,role:String?) {
        viewModelScope.launch {
            try {
                client.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email){
                    email = userEmail
                    password = userPassword
                    data = buildJsonObject {
                        put("name", JsonPrimitive(userName))
                        if (!userPhone.isNullOrBlank()) put("phone", JsonPrimitive(userPhone))
                        put("role", JsonPrimitive(role))
                        if (!role.isNullOrBlank()) put("role", JsonPrimitive(role))
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
                client.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
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
                client.auth.signOut()
                _userState.value = UserState.Success("User Logged Out Successfully")
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message.toString())
            }
        }
    }

    fun checkUserLoggedIn() {
        viewModelScope.launch {
            try {
                val session = client.auth.currentSessionOrNull()
                _userState.value = if (session != null) {
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
            client.auth.sessionStatus.collectLatest { session ->
                _userState.value = if (session != null) {
                    UserState.Success("User is logged in")
                } else {
                    UserState.Error("User is not logged in")
                }
            }
        }
    }

    fun createClientProfileBucket() {
        viewModelScope.launch {
            try {
                val session = client.auth.currentSessionOrNull()
                val bucketName = session?.user?.userMetadata?.get("email")?.jsonPrimitive?.content ?: throw Exception("User email not found")

                val existingBuckets = client.storage.retrieveBuckets()
                if (existingBuckets.any { it.id == bucketName }) {
                    _userState.value = UserState.Success("Bucket '$bucketName' already exists")
                    return@launch
                }

                client.storage.createBucket(id = bucketName) {
                    public = false
                    fileSizeLimit = 10.megabytes
                }

                _userState.value = UserState.Success("Client Profile Bucket '$bucketName' Created Successfully")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Bucket creation failed: ${e.message}")
            }
        }
    }


    suspend fun uploadClientImageToSupabase(context: Context, imageUri: Uri) {
        try {
            val session = client.auth.currentSessionOrNull()
            val bucketName = session?.user?.userMetadata?.get("email")?.jsonPrimitive?.content ?: throw Exception("User email not found")

            val imageFileName = "${UUID.randomUUID()}.jpg"
            val bucket = client.storage[bucketName]

            val imageByteArray = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: throw IOException("Failed to read image file")

            try {
                bucket.delete(imageFileName)
            } catch (_: Exception) {
            }

            // Upload the new image
            bucket.upload(imageFileName, imageByteArray){
                upsert = true
            }

            client.auth.updateUser {
                data = buildJsonObject {
                    put("profile_image", JsonPrimitive(imageFileName))
                }
            }

            _userState.value = UserState.Success("Image uploaded successfully to bucket: $bucketName")
        } catch (e: Exception) {
            _userState.value = UserState.Error("Image upload failed: ${e.message}")
        }
    }

    fun fetchClientImage() {
        viewModelScope.launch {
            try {
                val session = client.auth.currentSessionOrNull()
                val bucketName = session?.user?.userMetadata?.get("email")?.jsonPrimitive?.content ?: throw Exception("User email not found")
                val imageFileName = session.user?.userMetadata?.get("profile_image")?.jsonPrimitive?.content

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

    fun updateUserMetaData(name: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                client.auth.updateUser {
                    data = buildJsonObject {
                        put("name", JsonPrimitive(name))
                        put("email", JsonPrimitive(email))
                        put("phone", JsonPrimitive(phone))
                    }
                }
            }catch (e: Exception) {
                _userState.value = UserState.Error("Failed to update user metadata: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String,password: String){
        viewModelScope.launch {
            try{
                client.auth.resetPasswordForEmail(email)
                client.auth.updateUser {
                    this.password = password
                }
                _userState.value = UserState.Success("Password reset email sent successfully")
        }catch (e: Exception){
            _userState.value = UserState.Error(e.message.toString())
        }

        }
    }
}