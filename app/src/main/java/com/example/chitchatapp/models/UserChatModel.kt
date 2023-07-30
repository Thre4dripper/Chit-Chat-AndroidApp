package com.example.chitchatapp.models

import com.example.chitchatapp.enums.UserStatus

data class ChatModel(
    val chatId: String,
    val dmChatUser1: DMChatUserModel,
    val dmChatUser2: DMChatUserModel,
    val chatMessages: List<ChatMessageModel>,
    val mutedBy: List<String>
) {
    constructor() : this(
        "",
        DMChatUserModel(),
        DMChatUserModel(),
        listOf(),
        listOf()
    )
}

data class DMChatUserModel(
    val username: String,
    val profileImage: String,
    val status: String,
) {
    constructor() : this("", "", UserStatus.Online.name)
}