package com.example.agrimata.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.agrimata.model.UserState
import com.example.agrimata.viewmodels.FarmersAuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInFarmerAccount(onNavigateToSignUp: () -> Unit, onSignInSuccess: () -> Unit, authViewModel: FarmersAuthViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val userState = authViewModel.userState.value
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var togglePasswordVisibility by remember { mutableStateOf(false) }

    // Use MaterialTheme color scheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    LaunchedEffect(Unit) {
        isLoading = true
        authViewModel.checkUserLoggedIn()
        isLoading = false
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .background(primaryColor)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = "shoppingBasket",
                            tint = secondaryColor,

                            )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "AgriMata",
                            color = textColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }

                    Box(Modifier.align(Alignment.CenterHorizontally)){
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = "shoppingBasket",
                            tint = secondaryColor,
                            modifier = Modifier.align(Alignment.Center)
                                .width(100.dp)
                                .height(100.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = " Start selling smarter and growing bigger today! \uD83D\uDE9C\uD83C\uDF31",
                            color = textColor,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.weight(.5f))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = textColor) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = secondaryColor,
                            unfocusedTextColor = secondaryColor
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = textColor) },
                        visualTransformation = if (!togglePasswordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    togglePasswordVisibility = !togglePasswordVisibility
                                }
                            ) {
                                Icon(
                                    imageVector = if (togglePasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = textColor
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(.5f))

                    Button(
                        onClick = {
                            scope.launch {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    authViewModel.signInFarmer(email, password)
                                    if (userState is UserState.Success) {
                                        onSignInSuccess()
                                        Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        if (userState is UserState.Error){
                                            Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(primaryColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text("Sign In", color = textColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { onNavigateToSignUp() }) {
                        Text("Create an Account", style = MaterialTheme.typography.bodyMedium, color = textColor)
                    }

                    if (error.isNotEmpty()) {
                        Text(text = error, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}