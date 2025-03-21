package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.io.IOException
import java.util.UUID

class FarmerProductViewModel : ViewModel() {

    private val bucketName = "product-images"

    private val _farmerProducts = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val farmerProducts: StateFlow<List<FarmerProduct>> = _farmerProducts

    private val _productUploadState = MutableStateFlow("")
    val productUploadState: StateFlow<String> = _productUploadState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _productImage = mutableStateOf<ByteArray?>(null)
    val productImage: State<ByteArray?> = _productImage

    private val _productImageUri = mutableStateOf<Uri?>(null)
    val productImageUri: State<Uri?> = _productImageUri



    // Upload image to Supabase Storage and return image URL
    private suspend fun uploadImageToSupabase(context: Context, imageUri: Uri): String {
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
        return imageFileName
    }

    // Add a new farmer product with an image
    fun addFarmerProduct(context: Context, farmerProduct: FarmerProduct, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Uploading Image..."

                val imageFileName = uploadImageToSupabase(context, imageUri) // Now returns filename only

                val productWithImage = farmerProduct.copy(
                    productId = UUID.randomUUID().toString(),
                    imageUrl = imageFileName // Store only filename
                )

                _productUploadState.value = "Saving Product Data..."
                client.postgrest["farmproducts"].insert(productWithImage)

                _productUploadState.value = "Product Added Successfully"
                fetchFarmerProducts()
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Fetch the product image as a ByteArray
    fun fetchProductImage(imageFileName: String) {
        viewModelScope.launch {
            try {
                val bucket = client.storage[bucketName]
                val byteArray = bucket.downloadAuthenticated(imageFileName)
                _productImage.value = byteArray
            } catch (e: Exception) {
                _errorState.value = "Error fetching product image: ${e.message}"
            }
        }
    }

    // Fetch all farmer products
    fun fetchFarmerProducts() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Fetching Products..."

                val products = client.postgrest["farmproducts"].select().decodeList<FarmerProduct>()
                    .map { it.copy(imageUrl = "https://your-supabase-url/storage/v1/object/public/$bucketName/${it.imageUrl}")
                    }
                _farmerProducts.value = products

                _productUploadState.value = "Products Loaded Successfully"
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Delete a farmer product
    fun deleteFarmerProduct(productId: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Deleting Product..."

                val imageFileName = imageUrl.substringAfterLast("/")
                client.storage[bucketName].delete(imageFileName)

                client.postgrest["farmproducts"].delete {
                    filter { eq("productId", productId) }
                }

                _productUploadState.value = "Product Deleted Successfully"
                fetchFarmerProducts() // Refresh the list
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }


    // Update an existing farmer product
    fun updateFarmerProduct(context: Context, updatedProduct: FarmerProduct, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
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
                    filter { eq("productId", updatedProduct.productId) }
                }

                _productUploadState.value = "Product Updated Successfully"
                fetchFarmerProducts() // Refresh the list
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }
}
