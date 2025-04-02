package com.example.agrimata.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
    var productImages by remember { mutableStateOf<ByteArray?>(null) }
    var productUnit by remember { mutableStateOf("") }
    var productPrice by remember { mutableDoubleStateOf(0.0) }
    var totalPrice by remember { mutableDoubleStateOf(0.0) }
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            Color.White
        ),
        startY = 0f,
        endY = 500f
    )
    val marketViewModel: FarmerProductViewModel = viewModel()
    val cartItemTally = marketViewModel.cartItem.collectAsState()
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
            .fillMaxSize(),
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
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                marketViewModel.incrementCartItem()
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            if (cartItemTally.value <= 0) "1" else "${cartItemTally.value}",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = {
                                marketViewModel.decrementCartItem()
                            }
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .background(gradient)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            product?.let {
                LaunchedEffect(Unit) {
                    val bucketName = "product-images"
                    val bucket = client.storage[bucketName]
                    val productImage = bucket.downloadAuthenticated(it.imageUrl)
                    productImages = productImage
                }
                Text(
                    it.name, style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(16.dp))
                if (productImages != null) {
                    AsyncImage(
                        model = productImages,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(250.dp)
                            .clip(CircleShape),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }
                productPrice = it.pricePerUnit
                productUnit = it.unit
                Spacer(Modifier.weight(.5f))
                Column(
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    DetailView(Icons.Default.Category, it.category)
                    DetailView(Icons.Default.Description, it.description)
                    DetailView(Icons.Default.Pix, it.unit)
                    DetailView(Icons.Default.LocationOn, it.location)


                }
            } ?: CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
                    .size(150.dp)
            )
            Spacer(Modifier.weight(.5f))

            Button(
                onClick = {},
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
            ) {
                totalPrice = if (cartItemTally.value <= 0) {
                    productPrice * 1
                } else {
                    productPrice * cartItemTally.value
                }
                Text(
                    if (totalPrice == productPrice) "Purchase :$productPrice / $productUnit" else "Purchase :$totalPrice / $productUnit",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun DetailView(
    icon: ImageVector,
    title: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = "$title Icon", tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    ProductDetailScreen(navController = NavHostController(LocalContext.current))
}