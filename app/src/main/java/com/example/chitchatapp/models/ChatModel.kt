package com.example.chitchatapp.models

data class ChatModel(
    val chatId: String,
    val chatUsername1: String,
    val chatUsername2: String,
    val chatProfileImage1: String,
    val chatProfileImage2: String,
    val isFavorite: Boolean,
    val chatMessages: List<ChatMessageModel>,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        false,
        listOf(),
    )
}