// FarmerProductViewModel.kt
package com.example.agrimata.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.Farmer
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
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

    private val _farmProductDeals = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val farmProductDeals: StateFlow<List<FarmerProduct>> = _farmProductDeals

    private val _productsNearYou = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val productsNearYou: StateFlow<List<FarmerProduct>> = _productsNearYou

    private val _productByCategory = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val productByCategory: StateFlow<List<FarmerProduct>> = _productByCategory

    private val _productUploadState = MutableStateFlow("")
    val productUploadState: StateFlow<String> = _productUploadState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState




    init {
        fetchFarmerProducts()
        checkForDeals()
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

    fun fetchFarmProductByCategory(category: String){
        viewModelScope.launch {
            try {
               val productsbyCategory = client.postgrest["farmproducts"].select(){
                  filter {
                      eq("category",category)
                  }
              }.decodeList<FarmerProduct>()
                _productByCategory.value = productsbyCategory
                _errorState.value = "Products fetched successfully"
            }catch (e: Exception){
                _errorState.value = "${e.message}"
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val filteredProducts = client.postgrest["farmproducts"]
                    .select(columns = Columns.list("id", "name", "category", "pricePerUnit", "imageUrl","unit","stockQuantity")) {
                        filter {
                            or {
                                ilike("name", "%${query.replace("%", "\\%").replace("_", "\\_")}%")
                                ilike("location", "%${query.replace("%", "\\%").replace("_", "\\_")}%")
                            }
                        }
                    }
                    .decodeList<FarmerProduct>()
                if(filteredProducts.isNotEmpty()){
                    _farmerProducts.value = filteredProducts
                }else{
                    _farmerProducts.value = _farmerProducts.value
                }
            } catch (e: Exception) {
            }
        }
    }

    fun productNearYou(location: String) {
        viewModelScope.launch {
            try {
                val productsNearYou = client.postgrest["farmproducts"]
                    .select(columns = Columns.list("id", "name", "category", "pricePerUnit", "imageUrl", "unit", "stockQuantity")) {
                        filter {
                            ilike("location", "%${location.replace("%", "\\%").replace("_", "\\_")}%")
                        }
                    }
                    .decodeList<FarmerProduct>()

                if (productsNearYou.isNotEmpty()) {
                    _productsNearYou.value = productsNearYou
                } else {
                    _errorState.value = "No products near you"
                }
            } catch (e: Exception) {
                _errorState.value = "Error fetching products near you: ${e.message}"
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

    fun checkForDeals() {
        viewModelScope.launch {
            try {
                val products = client.postgrest["farmproducts"]
                    .select(columns = Columns.list("id", "name", "category", "pricePerUnit", "imageUrl","unit","stockQuantity"))
                    .decodeList<FarmerProduct>()

                val cheapestProducts = products
                    .groupBy { it.category }
                    .mapNotNull { (_, productsInCategory) ->
                        productsInCategory.minByOrNull { it.pricePerUnit }
                    }

                _farmProductDeals.value = cheapestProducts

            } catch (e: Exception) {
                println("Error fetching deals: ${e.message}")
            }
        }
    }
}