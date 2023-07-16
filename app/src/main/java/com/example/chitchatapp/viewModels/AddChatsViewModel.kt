package com.example.chitchatapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.chitchatapp.repository.AddChatsRepository

class AddChatsViewModel : ViewModel() {
    val searchedUsers = AddChatsRepository.searchResult

    fun searchUsers(
        searchQuery: String,
    ) = AddChatsRepository.searchUsers(searchQuery)

    fun addChat(
        chatUserId: String,
        onSuccess: (Boolean) -> Unit,
    ) = AddChatsRepository.addChat(chatUserId, onSuccess)
}