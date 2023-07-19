package com.example.chitchatapp.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.store.UserDetails

class ChattingViewModel : ViewModel() {

    fun getLoggedInUsername(context: Context): String? {
        return UserDetails.getUsername(context)
    }

    fun getChatDetails(chatId: String): ChatModel? {
        return ChatsRepository.homeChats.value?.find { chatModel ->
            chatModel.chatId == chatId
        }
    }

    fun getLiveChatDetails(chatId: String): LiveData<ChatModel?> {
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

    fun sendTextMessage(
        context: Context,
        chatId: String,
        text: String,
        chatMessageId: (String?) -> Unit,
    ) {
        val chatModel = getChatDetails(chatId) ?: return
        val from = getLoggedInUsername(context) ?: return
        val to = ChatUtils.getChatUsername(chatModel, from)
        ChatsRepository.sendTextMessage(chatModel, text, from, to, chatMessageId)
    }

    fun updateSeen(context: Context, chatId: String, onSuccess: (Boolean) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        ChatsRepository.updateSeen(context, chatModel, onSuccess)
    }
}