package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatType
import com.google.firebase.Timestamp

data class HomeChatModel(
    val id: String,
    val type: ChatType,
    val userChat: ChatModel?,
    val groupChat: GroupChatModel?,
    val lastMessageTimestamp: Timestamp
)