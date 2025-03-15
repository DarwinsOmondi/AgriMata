package com.example.agrimata.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.agrimata.viewmodels.FarmersAuthViewModel
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(onNavigateToSignUp: () -> Unit,onSignInSuccess: () -> Unit,authViewModel: FarmersAuthViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val userState = authViewModel.userState.value
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Sign In", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor =Color.Black,
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.Black
            ),
        )
        OutlinedTextField(
            value = password,onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor =Color.Black,
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.Black
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))



        Button(onClick = {
            scope.launch {
                isLoading = true
                
            }
            authViewModel.SignInFarmer(email, password)
            Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
            onSignInSuccess()
        }) {
            Text("Sign In")
        }

        TextButton(onClick = onNavigateToSignUp) {
            Text("Don't have an account? Sign Up")
        }
    }
}