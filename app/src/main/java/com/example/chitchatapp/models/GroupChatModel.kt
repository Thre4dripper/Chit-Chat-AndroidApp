package com.example.chitchatapp.models

data class GroupChatModel(
    val id: String,
    val name: String,
    val image: String?,
    val members: List<GroupChatUserModel>,
    val messages: List<GroupMessageModel>,
    val mutedBy: List<String>
) {
    constructor() : this("", "", null, listOf(), listOf(), listOf())
}

data class GroupChatUserModel(
    val username: String,
    val profileImage: String,
) {
    constructor() : this("", "")
}