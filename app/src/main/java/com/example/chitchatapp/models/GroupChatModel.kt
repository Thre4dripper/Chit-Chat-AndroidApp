package com.example.chitchatapp.models

data class GroupChatModel(
    val id: String,
    val name: String,
    val image: String?,
    val members: List<GroupChatUserModel>,
    val messages: List<GroupMessageModel>
) {
    constructor() : this("", "", null, listOf(), listOf())
}

data class GroupChatUserModel(
    val username: String,
    val profileImage: String,
) {
    constructor() : this("", "")
}