package com.example.agrimata.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BuyerMarketplaceViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<FarmerProduct>>(emptyList())
    val products: StateFlow<List<FarmerProduct>> = _products

    private val _orderState = MutableStateFlow<String>("")
    val orderState: StateFlow<String> = _orderState

    private val _productImage = mutableStateOf<ByteArray?>(null)
    val productImage: State<ByteArray?> = _productImage

    private val bucketName = "product-images"

    fun fetchAllProducts() {
        viewModelScope.launch {
            try {
                _orderState.value = "Fetching Products..."
                val productList = client.postgrest["farmproducts"].select().decodeList<FarmerProduct>()
                _products.value = productList
                _orderState.value = "Products Loaded Successfully"
            } catch (e: Exception) {
                _orderState.value = "Error: ${e.message}"
            }
        }
    }

    fun fetchProductImage(productId: String) {
        viewModelScope.launch {
            try {
                _orderState.value = "Fetching Product Image..."

                // Fetch the product to get its image URL
                val product = client.postgrest["farmproducts"].select {
                    filter { eq("productId", productId) }
                }.decodeSingle<FarmerProduct>()

                // Extract the filename from the URL
                val imageFileName = product.imageUrl.substringAfterLast("/")

                // Download image from Supabase Storage
                val bucket = client.storage[bucketName]
                val byteArray = bucket.downloadAuthenticated(imageFileName)

                _productImage.value = byteArray
                _orderState.value = "Product Image Loaded Successfully"
            } catch (e: Exception) {
                _orderState.value = "Error: ${e.message}"
            }
        }
    }


    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                _orderState.value = "Searching Products..."
                val filteredProducts = client.postgrest["farmproducts"].select {
                    filter {
                        or {
                            ilike("name", "%$query%") // Partial match for better search results
                            eq("location", query)
                        }
                    }
                }.decodeList<FarmerProduct>()
                _products.value = filteredProducts
                _orderState.value = "Search Completed"
            } catch (e: Exception) {
                _orderState.value = "Error: ${e.message}"
            }
        }
    }

    fun placeOrder(userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                _orderState.value = "Placing Order..."
                val orderData = mapOf(
                    "userId" to userId,
                    "productId" to productId,
                    "quantity" to quantity
                )
                client.postgrest["orders"].insert(orderData)
                _orderState.value = "Order Placed Successfully"
            } catch (e: Exception) {
                _orderState.value = "Error: ${e.message}"
            }
        }
    }
}
