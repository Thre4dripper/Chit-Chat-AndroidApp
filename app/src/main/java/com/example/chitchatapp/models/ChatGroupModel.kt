package com.example.chitchatapp.models

data class ChatGroupModel(
    val id: String,
    val name: String,
    val image: String,
    val admin: String,
    val members: List<String>,
    val messages: List<ChatMessageModel>
)