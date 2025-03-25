package com.example.agrimata.screens

import android.content.Context
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.agrimata.R
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.components.UserDrawerMenu
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.viewmodels.AgriMataClientAuth
import com.example.agrimata.viewmodels.ProfileViewModel
import com.example.agrimata.viewmodels.PermissionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(
    navController: NavHostController
) {
    val profileViewModel: ProfileViewModel = viewModel()
    val permissionViewModel: PermissionViewModel = viewModel()
    val viewmodel: AgriMataClientAuth = viewModel()
    val userProfileState = profileViewModel.userProfileState.value
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val profileImage = viewmodel.profileImage.value


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
            UserDrawerMenu(navController, drawerState) {
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
                    modifier = Modifier
                        .background(primaryColor)
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
                UserBottomNavigationBarUi(navController)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                when (userProfileState) {
                    is UserProfileState.Loading -> {
                        LoadingState()
                    }

                    is UserProfileState.Success -> {

                        if (profileImage != null) {
                            AsyncImage(
                                model = profileImage,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = R.drawable.baseline_person_24,
                                contentDescription = "Profile Placeholder",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        ProfileInfo(
                            name = userProfileState.name,
                            email = userProfileState.email,
                            phone = userProfileState.phone,
                            imageUrl = userProfileState.imageUrl,
                            userLocation,
                            permissionViewModel,
                            context
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
fun ProfileInfo(
    name: String,
    email: String,
    phone: String,
    imageUrl: String,
    userLocation: Location?,
    permissionViewModel: PermissionViewModel = viewModel(),
    context: Context
) {
    val decodeLocation = permissionViewModel.decodeLocation(context, userLocation)
    val textColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier
            .fillMaxWidth()
            .align(Alignment.Start)
            .padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile Icon",
                tint = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Name", color = textColor)
                Text(text = name, color = textColor)
            }
        }
        Row(Modifier
            .fillMaxWidth()
            .align(Alignment.Start)
            .padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email Icon",
                tint = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Email", color = textColor)
                Text(text = email, color = textColor)
            }
        }
        Row(Modifier
            .fillMaxWidth()
            .align(Alignment.Start)
            .padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = "Phone Icon",
                tint = textColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Phone", color = textColor)
                Text(text = phone, color = textColor)
            }
        }
        if (userLocation != null) {
            Row(Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(16.dp)) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location Icon",
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Location", color = textColor)
                    Text(text = decodeLocation, color = textColor)
                }
            }
        } else {
            Row(Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(16.dp)) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location Icon",
                    tint = textColor
                )
                Column {
                    Text("Location", color = textColor)
                    Text(text = "Location not available", color = MaterialTheme.colorScheme.error)
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
fun ClientProfileScreenPreview() {
    ClientProfileScreen(navController = NavHostController(LocalContext.current))
}