package com.example.agrimata.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agrimata.model.UserState
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


    LaunchedEffect(Unit) {
        isLoading = true
        authViewModel.checkUserLoggedIn()
        isLoading = false
    }


    Column(
        Modifier.fillMaxWidth()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
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



        Button(
            onClick = {
                scope.launch { 
                    if (email.isNotBlank()&& password.isNotBlank()) {
                        authViewModel.signInFarmer(email, password)
                        Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                        if (userState is UserState.Success){
                            onSignInSuccess()
                        }else{
                            Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            ) {
            if (isLoading){
                CircularProgressIndicator()
            }else{
                Text("Sign In")
            }
        }
        Row {
            Text(
                "Don't have an account?"
                ,style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 14.dp))
            Spacer(modifier = Modifier.padding(4.dp))
            TextButton(onClick = onNavigateToSignUp) {
                Text( "Sign Up")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(onNavigateToSignUp = {}, onSignInSuccess = {}, authViewModel = FarmersAuthViewModel())
}
