package com.example.agrimata.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.agrimata.R
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.viewmodels.AgriMataClientAuth
import com.example.agrimata.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientEditProfileScreen(onBack: () -> Unit) {
    // Use MaterialTheme color scheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    val viewModel: AgriMataClientAuth = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val userProfileState = profileViewModel.userProfileState.value
    val context = LocalContext.current
    val userState = viewModel.userState
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val profileImage = viewModel.profileImage.value

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
            scope.launch {
                try {
                    viewModel.createClientProfileBucket()
                    viewModel.uploadClientImageToSupabase(context, uri)
                } catch (e: Exception) {
                    // Handle error (optional)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(
            onClick = {
                onBack()
            },
            Modifier.align(Alignment.Start)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back", tint = textColor)
        }
        Spacer(Modifier.weight(.5f))
        Text(
            "Edit Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = textColor
        )
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = profileImage ?: R.drawable.baseline_person_24,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit Profile Picture",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(.5f))
        LaunchedEffect(userProfileState) {
            if (userProfileState is UserProfileState.Success) {
                userName = userProfileState.name
                userEmail = userProfileState.email
                userPhone = userProfileState.phone
            }
        }

        Column {
            Text("Name", color = textColor, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = userName.trim('"'),
                onValueChange = { userName = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor
                ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("Email", color = textColor, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor
                ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("Phone", color = textColor, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = "0$userPhone",
                onValueChange = { userPhone = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = textColor
                ),
            )
        }


        Spacer(modifier = Modifier.weight(.5f))

        Button(
            onClick = {
                scope.launch {
                    try {
                        viewModel.updateUserMetaData(userName, userEmail, userPhone)
                    } catch (e: Exception) {

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

        ) {
            Text("Save changes")
        }
    }
}