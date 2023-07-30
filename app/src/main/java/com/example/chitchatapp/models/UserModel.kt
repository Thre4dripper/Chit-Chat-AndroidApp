package com.example.chitchatapp.models

import java.io.Serializable

data class UserModel(
    val uid: String,
    val username: String,
    val name: String,
    val profileImage: String,
    val bio: String,
    val status: String,
    val favourites: List<String>,
    val fcmToken: String,
    val groups: List<String>,
) : Serializable {
    constructor() : this("", "", "", "", "", "", listOf(), "", listOf())
}