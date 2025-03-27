package com.example.agrimata.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.agrimata.components.UserBottomNavigationBarUi
import com.example.agrimata.model.Message
import com.example.agrimata.model.UserState
import com.example.agrimata.network.SuparBaseClient.client
import com.example.agrimata.viewmodels.ChatViewModel
import com.example.agrimata.viewmodels.AgriMataClientAuth
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = viewModel(),
    authViewModel: AgriMataClientAuth = viewModel(),
    navController: NavHostController
) {
    val messages by chatViewModel.messages.collectAsState()
    val userState by authViewModel.userState
    val senderId = remember { mutableStateOf<String?>(null) }
    var receiverId by remember { mutableStateOf<String?>(null) }
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val users = remember { mutableStateListOf<String>() }


    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    // Extract sender ID from authenticated user
    LaunchedEffect(userState) {
        if (userState is UserState.Success) {
            val session = client.auth.currentSessionOrNull()
            senderId.value = session?.user?.id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat", color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        bottomBar = {
            UserBottomNavigationBarUi(navController)
        },

    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // User Selection
            Text(
                text = "Select User to Chat",
                style = MaterialTheme.typography.titleLarge,
                color = textColor
            )

            LazyColumn(modifier = Modifier.weight(0.5f)) {
                items(users) { userName ->
                    UserCard(userName = userName, onSelect = { receiverId = it })
                }
            }

            // Chat Messages (Only shown when a user is selected)
            if (receiverId != null) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        MessageItem(
                            message = message,
                            isMe = message.senderPhone == senderId.value
                        )
                    }
                }

                // Message Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp),
                        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (messageText.text.isNotBlank() && senderId.value != null && receiverId != null) {
                                chatViewModel.sendMessage(
                                    message = messageText.text,
                                    senderId = senderId.value!!,
                                    receiverId = receiverId!!
                                )
                                messageText = TextFieldValue("")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Send", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(userName: String, onSelect: (String) -> Unit) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect(userName) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}

@Composable
fun MessageItem(message: Message, isMe: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMe) primaryColor else secondaryColor,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.message,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
