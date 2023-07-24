package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.store.UserStore

class ChattingViewModel : ViewModel() {
    private val TAG = "ChattingViewModel"

    var oldChatDetails: ChatModel? = null
    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getChatDetails(chatId: String): ChatModel? {
        oldChatDetails = ChatsRepository.homeChats.value?.find { homeChat ->
            homeChat.id == chatId
        }?.userChat

        return oldChatDetails
    }

    fun getLiveChatDetails(chatId: String): LiveData<ChatModel?> {
        return Transformations.map(ChatsRepository.homeChats) { homeChats ->
            homeChats?.find { homeChat ->
                homeChat.id == chatId
            }?.userChat
        }
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

    fun sendImageMessage(
        context: Context,
        chatId: String,
        imageUri: Uri,
        chatMessageId: (String?) -> Unit,
    ) {
        val chatModel = getChatDetails(chatId) ?: return
        val from = getLoggedInUsername(context) ?: return
        val to = ChatUtils.getChatUsername(chatModel, from)
        ChatsRepository.sendImage(chatModel, imageUri, from, to, chatMessageId)
    }

    fun updateSeen(context: Context, chatId: String, onSuccess: (Boolean) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        ChatsRepository.updateSeen(context, chatModel, onSuccess)
    }

    fun updateUserStatus(
        context: Context,
        chatId: String,
        status: String,
        onSuccess: (String?) -> Unit
    ) {
        val chatModel = getChatDetails(chatId) ?: return
        val loggedInUsername = getLoggedInUsername(context) ?: return
        ChatsRepository.updateUserStatus(chatModel, loggedInUsername, status, onSuccess)
    }
}