package com.example.chitchatapp.models

import com.example.chitchatapp.enums.GroupMessageType
import com.google.firebase.Timestamp

data class GroupMessageModel(
    override val id: String,
    val type: GroupMessageType,
    override val text: String?,
    override val image: String?,
    override val sticker: String?,
    override val time: Timestamp,
    override val seenBy: List<String>,
    val from: String,
) : MessageModel() {
    constructor() : this(
        "",
        GroupMessageType.TypeCreatedGroup,
        "",
        "",
        "",
        Timestamp.now(),
        listOf(""),
        "",
    )
}