package com.example.agrimata.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.agrimata.R
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.roomdb.CartProduct
import com.example.agrimata.viewmodels.CartProductViewModel
import io.github.jan.supabase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartProductScreen(navHostController: NavHostController,cartViewModel: CartProductViewModel){
    val cartItems by cartViewModel.allProducts.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart", color = MaterialTheme.colorScheme.secondary) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
            )
        }
    ){innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)) {
            LazyColumn {
                items(cartItems){ item ->
                    CardProductView(product = item, cartViewModel = cartViewModel)
                }
            }
        }
    }
}

@Composable
fun CardProductView(
    product: CartProduct,
    cartViewModel: CartProductViewModel,
    onClickListener: () -> Unit = {},
) {
    var liked by remember { mutableStateOf(false) }
    val cartItems by cartViewModel.allProducts.collectAsState(initial = emptyList())
    val isAddedToBucket by remember {
        derivedStateOf { cartItems.any { it.productId == product.productId } }
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(Color.White),
        onClick = { onClickListener() }
    ) {
        var productImages by remember { mutableStateOf<ByteArray?>(null) }
        LaunchedEffect(Unit) {
            val bucketName = "product-images"
            val bucket = client.storage[bucketName]
            val productImage = bucket.downloadAuthenticated(product.imageUrl)
            productImages = productImage
        }
        Column(modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(productImages)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "${product.name} Image",
                modifier = Modifier
                    .size(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.baseline_image_24)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Price: $${product.pricePerUnit} / ${product.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Stock: ${product.stockQuantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        liked = !liked
                    },
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    )
                }
                IconButton(
                    onClick = {
                        if (isAddedToBucket) {
                            cartViewModel.deleteProduct(product.productId.toInt())
                        } else {
                            cartViewModel.insertProduct(
                                CartProduct(
                                    productId = product.productId,
                                    name = product.name,
                                    description = product.description,
                                    category = product.category,
                                    pricePerUnit = product.pricePerUnit,
                                    unit = product.unit,
                                    stockQuantity = product.stockQuantity,
                                    location = product.location,
                                    imageUrl = product.imageUrl,
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = "Add to Bucket",
                        tint = if (isAddedToBucket) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}