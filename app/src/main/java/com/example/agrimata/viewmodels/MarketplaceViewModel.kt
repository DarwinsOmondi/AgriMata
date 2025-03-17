package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class FarmerProductViewModel : ViewModel() {

    private val bucketName = "product-images"
    private val _farmerProducts = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val farmerProducts: StateFlow<List<FarmerProduct>> = _farmerProducts

    private val _productUploadState = MutableStateFlow("")
    val productUploadState: StateFlow<String> = _productUploadState

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> get() = _userState

    fun createBucket(name: String) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                client.storage.createBucket(id = name) {
                    public = false
                    fileSizeLimit = 10.megabytes
                }
                _userState.value = UserState.Success("Created bucket successfully")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToSupabase(context: Context, imageUri: Uri): String {
        val imageFileName = "${UUID.randomUUID()}.jpg"
        val bucket = client.storage[bucketName]

        return try {
            val imageByteArray = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                inputStream.readBytes()
            } ?: throw IOException("Failed to read image file")

            bucket.upload(imageFileName, imageByteArray, upsert = true)
            bucket.publicUrl(imageFileName)
        } catch (e: Exception) {
            throw IOException("Image upload failed: ${e.message}")
        }
    }

    fun addFarmerProduct(context: Context, farmerProduct: FarmerProduct, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _productUploadState.value = "Uploading Image..."
                val imageUrl = uploadImageToSupabase(context, imageUri)

                val productWithImage = farmerProduct.copy(
                    productId = UUID.randomUUID().toString(),
                    imageUrl = imageUrl
                )

                _productUploadState.value = "Saving Product Data..."
                client.postgrest["farmproducts"].insert(productWithImage)

                _productUploadState.value = "Product Added Successfully"
            } catch (e: Exception) {
                _productUploadState.value = "Error: ${e.message}"
            }
        }
    }


    fun fetchFarmerProducts() {
        viewModelScope.launch {
            try {
                _productUploadState.value = "Fetching Products..."
                val products = client.postgrest["farmproducts"].select(
                    columns = Columns.list("name", "description", "category",
                            "pricePerUnit", "unit", "stockQuantity", "location",
                            "imageUrl", "isAvailable")
                ).decodeList<FarmerProduct>()
                _farmerProducts.value = products
                _productUploadState.value = "Products Loaded Successfully"
            } catch (e: Exception) {
                _productUploadState.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteFarmerProduct(productId: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                _productUploadState.value = "Deleting Product..."
                val imageFileName = imageUrl.substringAfterLast("/")
                client.storage[bucketName].delete(imageFileName)

                client.postgrest["farmproducts"]
                    .delete {
                        eq("productId", productId)
                    }

                _productUploadState.value = "Product Deleted Successfully"
            } catch (e: Exception) {
                _productUploadState.value = "Error: ${e.message}"
            }
        }
    }


    fun updateFarmerProduct(
        context: Context,
        updatedProduct: FarmerProduct,
        newImageUri: Uri? = null
    ) {
        viewModelScope.launch {
            try {
                _productUploadState.value = "Updating Product..."

                var imageUrl = updatedProduct.imageUrl
                if (newImageUri != null) {
                    imageUrl = uploadImageToSupabase(context, newImageUri)

                    // Delete old image from storage
                    val oldImageFileName = updatedProduct.imageUrl.substringAfterLast("/")
                    client.storage[bucketName].delete(oldImageFileName)
                }

                // Update product in database
                val productToUpdate = updatedProduct.copy(imageUrl = imageUrl)
                client.postgrest["farmproducts"].update(productToUpdate) {
                        eq("productId", updatedProduct.productId)
                }

                _productUploadState.value = "Product Updated Successfully"
            } catch (e: Exception) {
                _productUploadState.value = "Error: ${e.message}"
            }
        }
    }
}
