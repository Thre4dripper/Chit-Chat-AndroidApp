package com.example.chitchatapp.models

import com.google.firebase.Timestamp

abstract class MessageModel(
    open val id: String,
    open val text: String?,
    open val image: String?,
    open val sticker: String?,
    open val time: Timestamp,
    open val seenBy: List<String>,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        Timestamp.now(),
        listOf(""),
    )
}