package com.example.agrimata.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.agrimata.model.CategoryItem
import com.example.agrimata.model.Farmer
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.model.listOfCategoryItems
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.viewmodels.AgriMataClientAuth
import com.example.agrimata.viewmodels.FarmerProductViewModel
import com.example.agrimata.viewmodels.FarmersAuthViewModel
import com.example.agrimata.viewmodels.PermissionViewModel
import com.example.agrimata.viewmodels.ProfileViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.jan.supabase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProductScreen(navController: NavHostController) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val permissionViewModel: PermissionViewModel = viewModel()
    val viewModel: FarmerProductViewModel = viewModel()
    val authViewModel: AgriMataClientAuth = viewModel()
    val userProfileState = profileViewModel.userProfileState.value
    val farmerProducts by viewModel.farmerProducts.collectAsState()
    val productsNearYou by viewModel.productsNearYou.collectAsState()
    val farmProductsOnDeal by viewModel.farmProductDeals.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    var searchValue by remember { mutableStateOf("") }
    val profileImage = authViewModel.profileImage.value
    var isRefreshing by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val decodeLocation = permissionViewModel.decodeLocation(context,userLocation)


    LaunchedEffect(Unit) {
        permissionViewModel.getUserLocation(context) { location ->
            userLocation = location
        }
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(Unit) {
        viewModel.fetchFarmerProducts()
        viewModel.checkForDeals()
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
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchFarmerProducts()
                isRefreshing = false
            }
        ) {
            Column(
                Modifier.padding(padding)
            ) {
                OutlinedTextField(
                    value = searchValue,
                    onValueChange = { searchValue = it },
                    label = { Text("Search", color = MaterialTheme.colorScheme.secondary) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.searchProducts(searchValue)
                            searchValue =""
                        }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (searchValue.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                )
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(listOfCategoryItems) { category ->
                            CategoryItem(category)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "New To Market",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.height(16.dp))
                    when {
                        loadingState -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        errorState != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = errorState!!, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        else -> {
                            LazyRow(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(farmerProducts.take(10).reversed()) { product ->
                                    ColumnProductItem(product = product)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Deals", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        reverseLayout = true
                    ) {
                        items(farmProductsOnDeal) { product ->
                            ColumnProductItem(product = product)
                        }
                    }

                    Text(text = "Near You",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        reverseLayout = true
                    ) {
                        items(productsNearYou) { product ->
                            ColumnProductItem(product = product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnProductItem(product: FarmerProduct) {
    var liked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(Color.White)
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
            Text(text = "Stock: ${product.stockQuantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(Modifier.align(Alignment.End)){
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

@Composable
fun CategoryItem(category: CategoryItem) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        onClick = {}
    ) {
        Column {
            Spacer(Modifier.height(8.dp))
            Text(
                text = category.category,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}