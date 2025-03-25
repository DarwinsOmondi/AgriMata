package com.example.agrimata.screens

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.agrimata.R
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.model.listOfCategoryItems
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.viewmodels.FarmerProductViewModel
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCategoryScreen(
    navController: NavHostController,
    viewModel: FarmerProductViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val productsByCategory = remember { mutableStateMapOf<String, List<FarmerProduct>>() }

    LaunchedEffect(Unit) {
        listOfCategoryItems.forEach { category ->
            coroutineScope.launch {
                try {
                    val products = client.postgrest["farmproducts"].select {
                        filter { eq("category", category.category) }
                    }.decodeList<FarmerProduct>()
                    productsByCategory[category.category] = products
                } catch (e: Exception) {

                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Product Categories",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            UserBottomNavigationBarUi(navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            listOfCategoryItems.forEach { category ->
                val products = productsByCategory[category.category] ?: emptyList()
                if (products.isNotEmpty()) {
                    Text(
                        text = category.category,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(products) { product ->
                            ColumnProductItemCategory(product, onClickListener = {})
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ColumnProductItemCategory(product: FarmerProduct, onClickListener: () -> Unit = {}) {
    var liked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(Color.White),
        onClick = { onClickListener }
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
            Row(Modifier.align(Alignment.End)) {
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
            }
        }
    }
}
