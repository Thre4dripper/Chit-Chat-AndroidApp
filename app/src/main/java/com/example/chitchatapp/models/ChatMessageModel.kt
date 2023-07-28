package com.example.chitchatapp.models

import com.example.chitchatapp.enums.ChatMessageType
import com.google.firebase.Timestamp

data class ChatMessageModel(
    override val id: String,
    val type: ChatMessageType,
    override val text: String?,
    override val image: String?,
    override val sticker: Int?,
    override val time: Timestamp,
    override val seenBy: List<String>,
    val from: String,
    val to: String,
) : MessageModel() {
    constructor() : this(
        "",
        ChatMessageType.TypeFirstMessage,
        null,
        null,
        null,
        Timestamp.now(),
        listOf(""),
        "",
        "",
    )
}