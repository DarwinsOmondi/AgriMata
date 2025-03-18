package com.example.agrimata.screens

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.agrimata.components.BottomNavigationBarUi
import com.example.agrimata.components.DrawerMenu
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.viewmodels.ClientProfileViewModel
import com.example.agrimata.viewmodels.PermissionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    navController: NavHostController
) {
    val profileViewModel: ClientProfileViewModel = viewModel()
    val permissionViewModel: PermissionViewModel = viewModel()
    val userProfileState = profileViewModel.userProfileState.value
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    // Use MaterialTheme color scheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary


    LaunchedEffect(Unit) {
        permissionViewModel.getUserLocation(context) { location ->
            userLocation = location
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            DrawerMenu(navController, drawerState) {
                scope.launch { drawerState.close() }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Client Profile", color = textColor) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        primaryColor,
                    ),
                    modifier = Modifier.background(primaryColor)
                        .padding(end = 8.dp),
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = textColor
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBarUi(navController)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (userProfileState) {
                    is UserProfileState.Loading -> {
                        LoadingState()
                    }

                    is UserProfileState.Success -> {
                        ProfileInfo(
                            name = userProfileState.name,
                            email = userProfileState.email,
                            phone = userProfileState.phone,
                            imageUrl = userProfileState.imageUrl
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LocationInfo(userLocation, permissionViewModel, context)
                    }

                    is UserProfileState.Error -> {
                        ErrorState(errorMessage = userProfileState.message)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Loading...")
    }
}

@Composable
fun ProfileInfo(name: String, email: String, phone: String, imageUrl: String) {
    val textColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(text = "Name: $name", color = textColor)
        Text(text = "Email: $email", color = textColor)
        Text(text = "Phone: $phone",color = textColor)
    }
}

@Composable
fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LocationInfo(userLocation: Location?, permissionViewModel: PermissionViewModel = viewModel(), context: Context) {
    val decodeLocation = permissionViewModel.decodeLocation(context,userLocation)
    val textColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userLocation != null) {
            Text(text = "Your Location: $decodeLocation", color = textColor)
        } else {
            Text(text = "Location not available", color = MaterialTheme.colorScheme.error)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ClientProfileScreenPreview() {
    ClientProfileScreen( navController = NavHostController(LocalContext.current))
}