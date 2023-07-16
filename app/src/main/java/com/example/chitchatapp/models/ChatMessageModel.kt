package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatMessageEnums

data class ChatMessageModel(
    val chatMessageId: String,
    val chatMessageType: ChatMessageEnums,
    val chatMessage: String?,
    val chatMessageImage: String?,
    val chatMessageSticker: String?,
    val chatMessageReply: ChatMessageModel?,
    val chatMessageTime: String,
    val chatMessageSeen: Boolean,
    val chatMessageSender: String,
)