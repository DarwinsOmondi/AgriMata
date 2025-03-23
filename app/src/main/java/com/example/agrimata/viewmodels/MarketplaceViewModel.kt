// FarmerProductViewModel.kt
package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.async
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

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _productImages = MutableStateFlow<ByteArray?>(null)
    val productImages: StateFlow<ByteArray?> = _productImages


    init {
        fetchFarmerProducts()
    }

    private suspend fun uploadImageToSupabase(context: Context, imageUri: Uri): String {
        val imageFileName = "${UUID.randomUUID()}.jpg"
        val bucket = client.storage[bucketName]
        val imageByteArray = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: throw IOException("Failed to read image file")

        bucket.upload(imageFileName, imageByteArray) {
            upsert = true
        }
        return imageFileName
    }

    fun addFarmerProduct(context: Context, farmerProduct: FarmerProduct, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Uploading Image..."

                val imageFileName = uploadImageToSupabase(context, imageUri)

                val productWithImage = farmerProduct.copy(
                    productId = UUID.randomUUID().toString(),
                    imageUrl = imageFileName
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

    fun fetchFarmerProducts() {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Fetching Products..."

                val products = client.postgrest["farmproducts"].select().decodeList<FarmerProduct>()

                _farmerProducts.value = products
                _productUploadState.value = "Products Loaded Successfully"
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }



    fun deleteFarmerProduct(productId: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Deleting Product..."

                client.storage[bucketName].delete(imageUrl)
                client.postgrest["farmproducts"].delete {
                    filter { eq("productId", productId) }
                }

                _productUploadState.value = "Product Deleted Successfully"
                fetchFarmerProducts()
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun updateFarmerProduct(context: Context, updatedProduct: FarmerProduct, newImageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                _errorState.value = null
                _productUploadState.value = "Updating Product..."

                var imageUrl = updatedProduct.imageUrl
                if (newImageUri != null) {
                    client.storage[bucketName].delete(updatedProduct.imageUrl)
                    imageUrl = uploadImageToSupabase(context, newImageUri)
                }

                val productToUpdate = updatedProduct.copy(imageUrl = imageUrl)
                client.postgrest["farmproducts"].update(productToUpdate) {
                    filter { eq("productId", updatedProduct.productId) }
                }

                _productUploadState.value = "Product Updated Successfully"
                fetchFarmerProducts()
            } catch (e: Exception) {
                _errorState.value = "Error: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }

}