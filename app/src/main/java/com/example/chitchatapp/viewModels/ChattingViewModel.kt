package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.repository.UserRepository
import com.example.chitchatapp.store.UserStore

class ChattingViewModel : ViewModel() {
    private val TAG = "ChattingViewModel"

    val userDetails = UserRepository.userDetails

    fun listenChatIsFavorite(
        loggedInUsername: String,
        onSuccess: (UserModel?) -> Unit
    ) {
        UserRepository.listenUserDetails(loggedInUsername, onSuccess)
    }

    fun listenUserStatus(chatId: String, loggedInUsername: String, onSuccess: (String?) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        val username = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
        UserRepository.listenUserDetails(username) {
            onSuccess(it?.status)
        }
    }

    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getChatDetails(chatId: String): ChatModel? {
        return ChatsRepository.homeChats.value?.find { homeChat ->
            homeChat.id == chatId
        }?.userChat
    }

    fun getLiveChatDetails(chatId: String): LiveData<ChatModel?> {
        return ChatsRepository.homeChats.map { homeChats ->
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
        val to = ChatUtils.getUserChatUsername(chatModel, from)
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
        val to = ChatUtils.getUserChatUsername(chatModel, from)
        ChatsRepository.sendImage(chatModel, imageUri, from, to, chatMessageId)
    }

    fun updateSeen(context: Context, chatId: String, onSuccess: (Boolean) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        ChatsRepository.updateSeen(context, chatModel, onSuccess)
    }

    fun favouriteChat(userModel: UserModel, favourite: String, onSuccess: (Boolean?) -> Unit) {
        ChatsRepository.favouriteChat(userModel, favourite, onSuccess)
    }

    fun clearChat(chatId: String, onSuccess: (Boolean) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        ChatsRepository.clearChat(chatModel, onSuccess)
    }

    fun deletedChat(chatId: String, onSuccess: (Boolean) -> Unit) {
        val chatModel = getChatDetails(chatId) ?: return
        ChatsRepository.deleteChat(chatModel, onSuccess)
    }
}