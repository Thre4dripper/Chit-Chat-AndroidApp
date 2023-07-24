package com.example.chitchatapp.models

import com.example.chitchatapp.enums.HomeLayoutType
import com.google.firebase.Timestamp

data class HomeChatModel(
    val id: String,
    val type: HomeLayoutType,
    val userChat: ChatModel?,
    val groupChat: ChatGroupModel?,
    val lastMessageTimestamp: Timestamp
)