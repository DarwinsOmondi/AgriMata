package com.example.agrimata.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agrimata.model.UserProfileState
import com.example.agrimata.viewmodels.ClientProfileViewModel
import com.example.agrimata.viewmodels.FarmersAuthViewModel

@Composable
fun ClientProfileScreen(onLogOutSuccess: () -> Unit){
    val viewModel: ClientProfileViewModel = viewModel()
    val authViewModel: FarmersAuthViewModel = viewModel()
    val userProfileState = viewModel.userProfileState.value

    Column(Modifier.fillMaxSize()){
        Text(text = "Client Profile Screen")
        when (userProfileState) {
            is UserProfileState.Loading -> {
                Text(text = "Loading...")
            }
            is UserProfileState.Success -> {
                Text(text = "Name: ${userProfileState.name}")
                Text(text = "Email: ${userProfileState.email}")
                Text(text = "Phone: ${userProfileState.phone}")
                Text(text = "Image URL: ${userProfileState.imageUrl}")
            }
            is UserProfileState.Error -> {
                Text(text = "Error: ${userProfileState.message}")
            }
        }

        Button(
            onClick = {
                authViewModel.logOut()
            }
        ) {
            Text("Log out")
        }
    }
}