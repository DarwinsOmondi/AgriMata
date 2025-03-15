package com.example.agrimata

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
import com.example.agrimata.ui.theme.AgriMataTheme
import com.example.agrimata.viewmodels.FarmerProductViewModel
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.screens.SignInScreen
import com.example.agrimata.screens.SignUpScreen
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
    val authViewModel: FarmersAuthViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "signin"
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
    }
}


@Composable
fun ProductScreen(
    farmerViewModel: FarmerProductViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val productUploadState by farmerViewModel.productUploadState.collectAsState()
    val farmerProducts by farmerViewModel.farmerProducts.collectAsState()
    val listOfFarmProducts:MutableList<FarmerProduct> = farmerProducts.toMutableList()

    LaunchedEffect(Unit) {
        farmerViewModel.fetchFarmerProducts()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
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

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Pick Image")
            }
            Button(
                onClick = {
                    imageUri?.let { uri ->
                        val newProduct = FarmerProduct(
                            productId = UUID.randomUUID().toString(),
                            name = "Organic Avocados",
                            description = "Fresh farm avocados",
                            category = "Fruits",
                            pricePerUnit = 2.0,
                            unit = "kg",
                            stockQuantity = 30,
                            location = "Nairobi",
                            imageUrl = "ksdjfnisdufybfjsdosndivus",
                            isAvailable = true
                        )
                        farmerViewModel.addFarmerProduct(context, newProduct, uri)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("Add Product", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { farmerViewModel.fetchFarmerProducts() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Fetch Products", color = Color.White)
            }
            Button(
                onClick = { farmerViewModel.createBucket("product-images") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Create Bucket", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(productUploadState, color = Color.Blue, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))
    }
    Text("Farm Products $farmerProducts", fontWeight = FontWeight.Bold, fontSize = 22.sp)
}