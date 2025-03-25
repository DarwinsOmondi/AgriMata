package com.example.agrimata.screens

import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.agrimata.R
import com.example.agrimata.components.FarmerBottomNavigationBarUi
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.FarmerProduct
import com.example.agrimata.viewmodels.FarmerProductViewModel
import com.example.agrimata.viewmodels.FarmersAuthViewModel
import com.example.agrimata.viewmodels.PermissionViewModel
import com.example.agrimata.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navcontroller: NavHostController) {
    val viewModel: FarmerProductViewModel = viewModel()
    val permissionViewModel: PermissionViewModel = viewModel()
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productUnit by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var productLocation by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val authViewModel: FarmersAuthViewModel = viewModel()
    val profileImage = authViewModel.profileImage.value


    var categoryDropDownExpanded by remember { mutableStateOf(false) }
    var unitDropDownExpanded by remember { mutableStateOf(false) }
    var locationDialogExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionViewModel.getUserLocation(context) { location ->
            userLocation = location
        }
    }

    val decodeLocation = permissionViewModel.decodeLocation(context, userLocation)

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImageUri = it
        }


    // Use MaterialTheme color scheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

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
                label = { Text("Product Name", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Product Description", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productCategory,
                onValueChange = { productCategory = it },
                label = { Text("Category", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { categoryDropDownExpanded = true }
                    ) {
                        Icon(
                            Icons.Default.ArrowDropDownCircle,
                            contentDescription = "Select Category",
                            tint = primaryColor
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = categoryDropDownExpanded,
                onDismissRequest = { categoryDropDownExpanded = false }
            ) {
                listOfCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            productCategory = category
                            categoryDropDownExpanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Price", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productUnit,
                onValueChange = { productUnit = it },
                label = { Text("Unit (e.g., kg, pcs)", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            unitDropDownExpanded = true
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowDropDownCircle,
                            contentDescription = "Select Unit",
                            tint = primaryColor
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = unitDropDownExpanded,
                onDismissRequest = { unitDropDownExpanded = false },
            ) {
                listOfUnites.forEach { unites ->
                    DropdownMenuItem(
                        text = { Text(text = unites) },
                        onClick = {
                            productUnit = unites
                            unitDropDownExpanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productStock,
                onValueChange = { productStock = it },
                label = { Text("Stock Quantity", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = productLocation,
                onValueChange = { productLocation = it },
                label = { Text("Location", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            locationDialogExpanded = true
                        }
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Select Location",
                            tint = primaryColor
                        )
                    }
                }
            )
            if (locationDialogExpanded) {
                AlertDialog(
                    onDismissRequest = { locationDialogExpanded = false },
                    title = { Text("Where is the produce") },
                    text = {
                        Text("Select your location method.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                productLocation = decodeLocation
                                locationDialogExpanded = false
                            }
                        ) {
                            Text("Use Current Location")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                locationDialogExpanded = false
                                productLocation = ""
                            }
                        ) {
                            Text("Enter Manually")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (productName.isNotEmpty() && productDescription.isNotEmpty() && productPrice.isNotEmpty() && productCategory.isNotEmpty() && productUnit.isNotEmpty() && productStock.isNotEmpty() && productLocation.isNotEmpty() && selectedImageUri != null) {
                        val product = FarmerProduct(
                            productId = "",
                            name = productName,
                            description = productDescription,
                            category = productCategory,
                            pricePerUnit = productPrice.toDoubleOrNull() ?: 0.0,
                            unit = productUnit,
                            stockQuantity = productStock.toIntOrNull() ?: 0,
                            location = productLocation.toString(),
                            imageUrl = "",
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

val listOfCategories = listOf<String>(
    "Fresh Produce",
    "Meat & Poultry",
    "Dairy & Eggs",
    "Grains & Cereals",
    "Legumes & Nuts",
    "Honey & Natural Sweeteners",
    "Organic & Specialty Foods",
    "Oils & Condiments",
    "Farm Supplies & Tools",
    "Handmade & Artisanal Goods",
    "Flowers & Plants",
    "Livestock & Poultry"
)
val listOfUnites = listOf<String>(
    "kg",
    "g",
    "l",
    "pcs",
    "dozen",
    "Bags",
    "Packs",
    "Bunch",
    "tray"
)