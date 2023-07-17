package com.example.chitchatapp.models

data class ChatModel(
    val chatId: String,
    val dmChatUser1: DMChatUserModel,
    val dmChatUser2: DMChatUserModel,
    val isFavorite: Boolean,
    val chatMessages: List<ChatMessageModel>,
) {
    constructor() : this(
        "",
        DMChatUserModel(),
        DMChatUserModel(),
        false,
        listOf(),
    )
}

data class DMChatUserModel(
    val username: String,
    val profileImage: String,
) {
    constructor() : this("", "")
}