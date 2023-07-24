package com.example.chitchatapp.models

import com.example.chitchatapp.enums.HomeLayoutType

data class HomeChatModel(
    val id: String,
    val type: HomeLayoutType,
    val userChat: ChatModel?,
    val groupChat: ChatGroupModel?,
)