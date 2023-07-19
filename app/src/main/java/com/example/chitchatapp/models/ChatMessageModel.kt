package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatMessageType
import com.google.firebase.Timestamp

data class ChatMessageModel(
    val id: String,
    val type: ChatMessageType,
    val text: String?,
    val image: String?,
    val sticker: String?,
    val reply: ChatMessageModel?,
    val time: Timestamp,
    val seenBy: List<String>,
    val from: String,
    val to: String,
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