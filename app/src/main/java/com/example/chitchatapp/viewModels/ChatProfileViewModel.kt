package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.UserChatsRepository
import com.example.chitchatapp.repository.UserRepository

class ChatProfileViewModel : ViewModel() {

    fun getNameAndBio(
        chatUsername: String,
        onSuccess: (String, String) -> Unit
    ) {
        UserRepository.listenUserDetails(chatUsername) {
            if (it == null) {
                onSuccess("", "")
                return@listenUserDetails
            }
            onSuccess(it.name, it.bio)
        }
    }

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

    fun muteUnMuteChat(
        chatModel: ChatModel,
        loggedInUsername: String,
        mute: Boolean,
        onSuccess: (Boolean) -> Unit
    ) = UserChatsRepository.muteUnMuteChat(chatModel, loggedInUsername, mute, onSuccess)
}