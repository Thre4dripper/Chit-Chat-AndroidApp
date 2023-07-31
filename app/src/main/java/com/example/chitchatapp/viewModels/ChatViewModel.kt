package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.UserChatsRepository
import com.example.chitchatapp.repository.UserRepository
import com.example.chitchatapp.store.UserStore

class ChatViewModel : ViewModel() {
    private val TAG = "ChatViewModel"

    private var _chatDetails = MutableLiveData<ChatModel?>(null)
    val chatDetails: LiveData<ChatModel?>
        get() = _chatDetails

    fun listenChatIsFavorite(
        loggedInUsername: String, onSuccess: (UserModel?) -> Unit
    ) {
        UserRepository.listenUserDetails(loggedInUsername, onSuccess)
    }

    fun listenUserStatus(loggedInUsername: String, onSuccess: (String?) -> Unit) {
        val chatModel = _chatDetails.value ?: return
        val username = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
        UserRepository.listenUserDetails(username) {
            onSuccess(it?.status)
        }
    }

    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getChatDetails(chatId: String, onSuccess: (ChatModel?) -> Unit) =
        UserChatsRepository.getUserChatById(chatId, onSuccess)


    fun getLiveChatDetails(chatId: String) {
        //always fetch latest chat detail
        UserChatsRepository.getLiveUserChatById(chatId) {
            _chatDetails.value = it
        }
    }

    fun createNewChat(
        newChatUser: UserModel,
        chatId: (String?) -> Unit,
    ) = AddChatsRepository.addChat(newChatUser, chatId)

    fun sendTextMessage(
        context: Context,
        text: String,
        chatMessageId: (String?) -> Unit,
    ) {
        val chatModel = _chatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        val to = ChatUtils.getUserChatUsername(chatModel, from)
        UserChatsRepository.sendTextMessage(context, chatModel, text, from, to, chatMessageId)
    }

    fun sendImageMessage(
        context: Context,
        imageUri: Uri,
        chatMessageId: (String?) -> Unit,
    ) {
        val chatModel = _chatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        val to = ChatUtils.getUserChatUsername(chatModel, from)
        UserChatsRepository.sendImage(context, chatModel, imageUri, from, to, chatMessageId)
    }

    fun sendSticker(
        context: Context,
        stickerIndex: Int,
        chatMessageId: (String?) -> Unit,
    ) {
        val chatModel = _chatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        val to = ChatUtils.getUserChatUsername(chatModel, from)
        UserChatsRepository.sendSticker(context, chatModel, stickerIndex, from, to, chatMessageId)
    }

    fun updateSeen(context: Context, onSuccess: (Boolean) -> Unit) {
        val chatModel = _chatDetails.value ?: return
        UserChatsRepository.updateSeen(context, chatModel, onSuccess)
    }

    fun favouriteChat(userModel: UserModel, favourite: String, onSuccess: (Boolean?) -> Unit) {
        UserChatsRepository.favouriteChat(userModel, favourite, onSuccess)
    }

    fun clearChat(onSuccess: (Boolean) -> Unit) {
        val chatModel = _chatDetails.value ?: return
        UserChatsRepository.clearChat(chatModel, onSuccess)
    }

    fun deletedChat(onSuccess: (Boolean) -> Unit) {
        val chatModel = _chatDetails.value ?: return
        UserChatsRepository.deleteChat(chatModel, onSuccess)
    }
}