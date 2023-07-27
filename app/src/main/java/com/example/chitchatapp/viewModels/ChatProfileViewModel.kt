package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.GroupsRepository

class ChatProfileViewModel : ViewModel() {
    fun commonGroups(
        chatUsername: String,
        chatUserImage: String,
        loggedInUsername: String,
        onSuccess: (List<GroupChatModel>) -> Unit
    ) {
        GroupsRepository.findCommonGroups(
            chatUsername,
            chatUserImage,
            loggedInUsername,
            onSuccess
        )
    }
}