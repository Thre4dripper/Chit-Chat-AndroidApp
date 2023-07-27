package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.repository.GroupsRepository

class GroupProfileViewModel : ViewModel() {

    fun findChatId(
        loggedInUsername: String,
        memberUsername: String,
        onSuccess: (String?) -> Unit
    ) = GroupsRepository.findMemberChatId(loggedInUsername, memberUsername, onSuccess)
}