package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.UserRepository

class ChatProfileViewModel : ViewModel() {
    fun commonGroups(
        chatUsername: String,
        chatUserImage: String,
        loggedInUsername: String,
        onSuccess: (List<GroupChatModel>) -> Unit
    ) {
        val loggedInUserImage = UserRepository.userDetails.value!!.profileImage
        GroupsRepository.findCommonGroups(
            chatUsername,
            chatUserImage,
            loggedInUsername,
            loggedInUserImage,
            onSuccess
        )
    }
}