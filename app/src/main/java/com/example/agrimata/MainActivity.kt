package com.example.agrimata

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.agrimata.ui.theme.AgriMataTheme
import com.example.agrimata.viewmodels.FarmerProductViewModel
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.model.UserState
import com.example.agrimata.screens.ClientEditProfileScreen
import com.example.agrimata.screens.ClientProfileScreen
import com.example.agrimata.screens.SignInScreen
import com.example.agrimata.screens.SignUpScreen
import com.example.agrimata.viewmodels.AgriMataClientAuth
import com.example.agrimata.viewmodels.FarmersAuthViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgriMataTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AgriMata(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AgriMata(modifier: Modifier) {
    val navController = rememberNavController()
    val authViewModel: AgriMataClientAuth = viewModel()
    var startDestination by remember { mutableStateOf("signin") }

    LaunchedEffect(Unit) {
        authViewModel.checkUserLoggedIn()
    }

    val userState by authViewModel.userState
    LaunchedEffect(userState) {
        startDestination = if (userState is UserState.Success) "profile" else "signin"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("signin") {
            SignInScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onSignInSuccess = { navController.navigate("product") },
                authViewModel = authViewModel
            )
        }
        composable("product") {
            ProductScreen()
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToSignIn = { navController.navigate("signin") },
                authViewModel = authViewModel
            )
        }
        composable("profile"){
            ClientProfileScreen(
                navController = navController,
            )
        }
        composable("editprofile"){
            ClientEditProfileScreen(
                onBack = {
                    navController.navigate("profile")
                }
            )
        }
    }
}

@Composable
fun ProductScreen(
    farmerViewModel: FarmerProductViewModel = viewModel(),
    authViewModel: AgriMataClientAuth = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val productUploadState by farmerViewModel.productUploadState.collectAsState()
    val farmerProducts by farmerViewModel.farmerProducts.collectAsState()


    LaunchedEffect(Unit) {
        farmerViewModel.fetchFarmerProducts()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Manage Your Products", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { farmerViewModel.fetchFarmerProducts() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Fetch Products", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(productUploadState, color = Color.Blue, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                authViewModel.LogOut()
            }
        ) {
            Text("Log out")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(farmerProducts) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
fun ProductCard(product: FarmerProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl ?: "https://via.placeholder.com/150",
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "Price: $${product.pricePerUnit} per ${product.unit}")
                Text(text = "Location: ${product.location}", fontSize = 14.sp)
                Text(
                    text = if (product.isAvailable) "Available" else "Out of Stock",
                    color = if (product.isAvailable) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
