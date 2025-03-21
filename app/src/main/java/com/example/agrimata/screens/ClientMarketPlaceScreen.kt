package com.example.agrimata.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.agrimata.R
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.viewmodels.FarmerProductViewModel


@Composable
fun FarmerProductScreen(navController: NavHostController) {
    val viewModel: FarmerProductViewModel = viewModel()
    val farmerProducts by viewModel.farmerProducts.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()


    // Fetch products when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchFarmerProducts()
    }

    Scaffold(
        bottomBar = {
            UserBottomNavigationBarUi(navController)
        }
    ) { padding ->
        if (loadingState) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorState != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorState!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(farmerProducts) { product ->
                    ProductItem(product = product)
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: FarmerProduct) {
    val viewModel: FarmerProductViewModel = viewModel()
    val profileImage = viewModel.productImage.value
    val imageUrl = if (product.imageUrl.isNotEmpty()) {
        product.imageUrl
    } else null


    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (imageUrl != null) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "Placeholder Image",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Price: $${product.pricePerUnit} per ${product.unit}")
            Text(text = "Stock: ${product.stockQuantity}")
        }
    }
}

