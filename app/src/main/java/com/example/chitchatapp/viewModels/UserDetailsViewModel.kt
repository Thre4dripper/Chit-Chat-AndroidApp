package com.example.chitchatapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.repository.UserDetailsRepository

class UserDetailsViewModel : ViewModel() {
    val userDetails = UserDetailsRepository.userDetails

    fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) =
        UserDetailsRepository.getUserDetails(context, onSuccess)


    fun updateUsername(
        username: String,
        callback: (String) -> Unit,
    ) = UserDetailsRepository.updateUsername(username, callback)
}