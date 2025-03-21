package com.example.agrimata.model

import java.util.UUID

data class Message(
    val id: String,
    val senderPhone: String,
    val receiverPhone: String,
    val message: String,
    val groupID: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDeleted: Boolean,
    val isEdited: Boolean
)
