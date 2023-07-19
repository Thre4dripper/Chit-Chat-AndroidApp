package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatMessageType
import com.google.firebase.Timestamp

data class ChatMessageModel(
    val chatMessageId: String,
    val chatMessageType: ChatMessageType,
    val chatMessage: String?,
    val chatMessageImage: String?,
    val chatMessageSticker: String?,
    val chatMessageReply: ChatMessageModel?,
    val chatMessageTime: Timestamp,
    val seenBy: List<String>,
    val chatMessageFrom: String,
    val chatMessageTo: String,
) {
    constructor() : this(
        "",
        ChatMessageType.TypeFirstMessage,
        "",
        "",
        "",
        null,
        Timestamp.now(),
        listOf(""),
        "",
        "",
    )
}