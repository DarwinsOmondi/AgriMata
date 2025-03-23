// FarmerProductScreen.kt
package com.example.agrimata.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.agrimata.R
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.viewmodels.AgriMataClientAuth
import com.example.agrimata.viewmodels.FarmerProductViewModel
import com.example.agrimata.viewmodels.ProfileViewModel
import io.github.jan.supabase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProductScreen(navController: NavHostController) {
    val profileViewModel: ProfileViewModel = viewModel()
    val viewModel: FarmerProductViewModel = viewModel()
    val userProfileState = profileViewModel.userProfileState.value
    val farmerProducts by viewModel.farmerProducts.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    var searchValue by remember { mutableStateOf("") }
    val authViewModel: AgriMataClientAuth = viewModel()
    val profileImage = authViewModel.profileImage.value

    LaunchedEffect(Unit) {
        viewModel.fetchFarmerProducts()
    }

    Scaffold(
        topBar = {
            when (userProfileState) {
                is UserProfileState.Success -> {
                    TopAppBar(
                        title = { Text(userProfileState.name) },
                        navigationIcon = {
                            AsyncImage(
                                model = profileImage,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                    )
                }

                is UserProfileState.Error -> TODO()
                UserProfileState.Loading -> TODO()
            }
        },
        bottomBar = {
            UserBottomNavigationBarUi(navController)
        }
    ) { padding ->
        Column(
            Modifier.padding(padding)
        ){
            OutlinedTextField(
                value = searchValue,
                onValueChange = { searchValue = it },
                label = { Text("Search") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Categories", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary,modifier = Modifier.padding(horizontal = 8.dp))

            Spacer(Modifier.height(16.dp))
            LazyRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listOfCategoryItems) { category ->
                    CategoryItem(category)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("New To Market", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary,modifier = Modifier.padding(horizontal = 8.dp))

            Spacer(Modifier.height(16.dp))
            when {
                loadingState -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                errorState != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorState!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(farmerProducts) { product ->
                            ColumnProductItem(product = product)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Deals", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary,modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ColumnProductItem(product: FarmerProduct) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        var productImages by remember { mutableStateOf<ByteArray?>(null) }
        LaunchedEffect(Unit) {
            val bucketName = "product-images"
            val bucket = client.storage[bucketName]
            val productImage = bucket.downloadAuthenticated(product.imageUrl)
            productImages = productImage
        }
        Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Price: $${product.pricePerUnit} / ${product.unit}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Stock: ${product.stockQuantity}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CategoryItem(category: CategoryItem) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column {

            Spacer(Modifier.height(8.dp))
            Text(
                text = category.category,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

sealed class CategoryItem(val category: String) {
    object Category1: CategoryItem("\uD83E\uDD66 Fresh Produce")
    object Category2: CategoryItem("\uD83C\uDF56 Meat & Poultry")
    object Category3: CategoryItem("\uD83E\uDD5B Dairy & Eggs")
    object Category4: CategoryItem("\uD83C\uDF3E Grains & Cereals")
    object Category5: CategoryItem("\uD83E\uDED8 Legumes & Nuts")
    object Category6: CategoryItem("\uD83C\uDF6F Honey & Natural Sweeteners")
    object Category7: CategoryItem("\uD83C\uDF3F Organic & Specialty Foods")
    object Category8: CategoryItem("\uD83C\uDF76 Oils & Condiments")
    object Category9: CategoryItem("\uD83C\uDF31 Farm Supplies & Tools")
    object Category10: CategoryItem("\uD83D\uDC04 Livestock & Poultry")
    object Category11: CategoryItem("\uD83C\uDF3B Flowers & Plants")
    object Category12: CategoryItem("\uD83D\uDECD Handmade & Artisanal Goods")
}

val listOfCategoryItems = listOf(
    CategoryItem.Category1,
    CategoryItem.Category2,
    CategoryItem.Category3,
    CategoryItem.Category4,
    CategoryItem.Category5,
    CategoryItem.Category6,
    CategoryItem.Category7,
    CategoryItem.Category8,
    CategoryItem.Category9,
    CategoryItem.Category10,
    CategoryItem.Category11,
    CategoryItem.Category12
)

