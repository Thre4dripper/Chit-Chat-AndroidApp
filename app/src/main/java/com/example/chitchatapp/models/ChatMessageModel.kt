package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatMessageEnums
import com.google.firebase.Timestamp

data class ChatMessageModel(
    val chatMessageId: String,
    val chatMessageType: ChatMessageEnums,
    val chatMessage: String?,
    val chatMessageImage: String?,
    val chatMessageSticker: String?,
    val chatMessageReply: ChatMessageModel?,
    val chatMessageTime: Timestamp,
    val chatMessageSeen: Boolean,
    val chatMessageFrom: String,
    val chatMessageTo: String,
)