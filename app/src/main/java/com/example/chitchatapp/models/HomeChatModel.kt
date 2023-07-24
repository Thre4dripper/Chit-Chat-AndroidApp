package com.example.chitchatapp.models

import com.example.chitchatapp.enums.HomeChatType

data class HomeChatModel(
    val id: String,
    val type: HomeChatType,
    val userChat: ChatModel?,
    val groupChat: ChatGroupModel?,
)