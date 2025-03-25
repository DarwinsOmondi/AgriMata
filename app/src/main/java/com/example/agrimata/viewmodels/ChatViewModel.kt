package com.example.agrimata.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.Message
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        // Fetch messages when ViewModel initializes
        viewModelScope.launch { fetchMessages() }
    }

    // Fetch messages from Supabase manually
    fun fetchMessages(senderId: String? = null, receiverId: String? = null) {
        viewModelScope.launch {
            try {
                val response = client.postgrest["messages"].select()
                val messagesList = Json.decodeFromString<List<Message>>(response.toString())

                // Filter messages based on sender and receiver
                val filteredMessages = messagesList.filter {
                    (senderId == null || it.senderPhone == senderId) &&
                            (receiverId == null || it.receiverPhone == receiverId)
                }

                _messages.value = filteredMessages
            } catch (e: Exception) {
                // Handle errors (e.g., log them or show a toast in UI)
            }
        }
    }

    fun sendMessage(
        message: String,
        senderId: String,
        receiverId: String,
        groupId: String? = null
    ) {
        viewModelScope.launch {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderPhone = senderId,
                receiverPhone = receiverId,
                message = message,
                groupID = groupId ?: "",
                isDeleted = false,
                isEdited = false
            )

            try {
                client.postgrest["messages"].insert(newMessage)
                fetchMessages(senderId, receiverId)  // Refresh messages after sending
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }
}
