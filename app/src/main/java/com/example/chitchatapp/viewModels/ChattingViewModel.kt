package com.example.chitchatapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.ChatsRepository

class ChattingViewModel : ViewModel() {

    fun getChatDetails(chatId: String): LiveData<ChatModel?> {
        return Transformations.map(ChatsRepository.homeChats) {
            it?.find { chatModel ->
                chatModel.chatId == chatId
            }
        } as MutableLiveData<ChatModel?>
    }

    fun createNewChat(
        newChatUser: UserModel,
        chatId: (String?) -> Unit,
    ) = AddChatsRepository.addChat(newChatUser, chatId)

}