package com.example.chitchatapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.ChatsRepository

class ChattingViewModel : ViewModel() {
    private val _chatDetails = MutableLiveData<ChatModel?>(null)
    val chatDetails: LiveData<ChatModel?>
        get() = _chatDetails

    fun getChatDetails(chatId: String) {
        val chat = ChatsRepository.homeChats.value?.find { it.chatId == chatId }
        _chatDetails.value = chat
    }

    fun createNewChat(
        newChatUser: UserModel,
        onSuccess: (Boolean) -> Unit,
    ) = AddChatsRepository.addChat(newChatUser, onSuccess)

}