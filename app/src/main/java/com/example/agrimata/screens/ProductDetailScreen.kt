package com.example.agrimata.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.viewmodels.FarmerProductViewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavHostController) {
    val marketViewModel: FarmerProductViewModel = viewModel()
    val productId = navController.currentBackStackEntry?.arguments?.getString("productId")

    var product by remember { mutableStateOf<FarmerProduct?>(null) }

    LaunchedEffect(productId) {
        if (productId.isNullOrEmpty()) return@LaunchedEffect

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newProduct = client.postgrest["farmproducts"].select {
                    filter { eq("productId", productId) }
                }.decodeList<FarmerProduct>()

                product = newProduct.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        marketViewModel.fetchFarmerProducts()
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        topBar = {
            TopAppBar(
                title = {
                    Text("Details", color = MaterialTheme.colorScheme.secondary)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            product?.let {
                DetailItem(it)
            } ?: CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
                    .size(150.dp)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun DetailItem(farmerProduct: FarmerProduct) {
    var productImages by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(Unit) {
        val bucketName = "product-images"
        val bucket = client.storage[bucketName]
        val productImage = bucket.downloadAuthenticated(farmerProduct.imageUrl)
        productImages = productImage
    }

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(0.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = farmerProduct.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(32.dp))
                }
            }
            if (productImages != null) {
                AsyncImage(
                    model = productImages,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .align(Alignment.BottomCenter),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(75.dp))

        Card(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = farmerProduct.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Price:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = farmerProduct.pricePerUnit.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    farmerProduct.category,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Unit:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    farmerProduct.unit,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Stock Quantity:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    farmerProduct.stockQuantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Location:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = farmerProduct.location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
