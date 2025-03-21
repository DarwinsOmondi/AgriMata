package com.example.agrimata.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.agrimata.R
import com.example.agrimata.components.FarmerBottomNavigationBarUi
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.viewmodels.FarmerProductViewModel

@Composable
fun AddProductScreen(navcontroller: NavHostController) {
    val viewModel: FarmerProductViewModel = viewModel()
    val context = LocalContext.current
    var productName by remember { mutableStateOf(TextFieldValue("")) }
    var productDescription by remember { mutableStateOf(TextFieldValue("")) }
    var productCategory by remember { mutableStateOf(TextFieldValue("")) }
    var productPrice by remember { mutableStateOf(TextFieldValue("")) }
    var productUnit by remember { mutableStateOf(TextFieldValue("")) }
    var productStock by remember { mutableStateOf(TextFieldValue("")) }
    var productLocation by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImageUri = it
        }

    Scaffold(
        bottomBar = {
            FarmerBottomNavigationBarUi(navcontroller)
        }
    ) { innerPadding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Product", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        // Image Picker
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            selectedImageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.baseline_image_24),
                contentDescription = "Placeholder Image",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text Fields
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productCategory,
            onValueChange = { productCategory = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productUnit,
            onValueChange = { productUnit = it },
            label = { Text("Unit (e.g., kg, pcs)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productStock,
            onValueChange = { productStock = it },
            label = { Text("Stock Quantity") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productLocation,
            onValueChange = { productLocation = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                if (productName.text.isNotEmpty() && productDescription.text.isNotEmpty() && productPrice.text.isNotEmpty() && productCategory.text.isNotEmpty() && productUnit.text.isNotEmpty() && productStock.text.isNotEmpty() && productLocation.text.isNotEmpty() && selectedImageUri != null) {
                    val product = FarmerProduct(
                        productId = "",
                        name = productName.text,
                        description = productDescription.text,
                        category = productCategory.text,
                        pricePerUnit = productPrice.text.toDoubleOrNull() ?: 0.0,
                        unit = productUnit.text,
                        stockQuantity = productStock.text.toIntOrNull() ?: 0,
                        location = productLocation.text,
                        imageUrl = "",
                        isAvailable = true
                    )
                    viewModel.addFarmerProduct(context, product, selectedImageUri!!)
                } else {
                    Toast.makeText(
                        context,
                        "Please fill all fields and select an image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Add Product")
        }
    }
}
}
