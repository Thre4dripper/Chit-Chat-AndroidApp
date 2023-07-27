package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.UserRepository

class GroupProfileViewModel : ViewModel() {

    fun findChatId(
        loggedInUsername: String,
        memberUsername: String,
        onSuccess: (String?) -> Unit
    ) = GroupsRepository.findMemberChatId(loggedInUsername, memberUsername, onSuccess)

    fun getUserModel(
        memberUsername: String,
        onSuccess: (UserModel?) -> Unit
    ) = UserRepository.listenUserDetails(memberUsername, onSuccess)
}