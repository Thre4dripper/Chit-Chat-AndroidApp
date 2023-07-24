package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.repository.UserRepository

class UserDetailsViewModel : ViewModel() {
    val userDetails = UserRepository.userDetails

    fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) =
        UserRepository.getUserDetails(context, onSuccess)


    fun updateUsername(
        username: String,
        callback: (String) -> Unit,
    ) = UserRepository.updateUsername(username, callback)

    fun updateName(
        context: Context,
        name: String,
        callback: (String) -> Unit,
    ) = UserRepository.updateName(context, name, callback)

    fun updateBio(
        context: Context,
        bio: String,
        callback: (String) -> Unit,
    ) = UserRepository.updateBio(context, bio, callback)

    fun updateProfilePicture(
        context: Context,
        profilePicture: Uri,
        callback: (String) -> Unit,
    ) = UserRepository.updateProfilePicture(context, profilePicture, callback)
}