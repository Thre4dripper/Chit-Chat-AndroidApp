package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.enums.HomeLayoutType
import com.example.chitchatapp.firebase.chats.GetChats
import com.example.chitchatapp.firebase.chats.GetGroupChats
import com.example.chitchatapp.firebase.chats.SendChat
import com.example.chitchatapp.firebase.chats.UpdateSeen
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.models.HomeChatModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ChatsRepository {
    companion object {
        private val TAG = "ChatsRepository"

        val homeChats = MutableLiveData<List<HomeChatModel>?>(null)

        fun getAllUserChats(
            context: Context
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(context) ?: ""
            GetChats.getAllUserChats(firestore, loggedInUser) { userChats ->
                val oldList = homeChats.value ?: emptyList()
                val updatedList = oldList.toMutableList()
                updatedList.removeIf { it.type == HomeLayoutType.USER }
                updatedList.addAll(
                    userChats.map {
                        HomeChatModel(it.chatId, HomeLayoutType.USER, it, null)
                    }
                )
                homeChats.value = updatedList
            }
        }

        fun getAllGroupChats(context: Context) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: ""
            val userImage = UserRepository.userDetails.value?.profileImage ?: ""

            val groupUserModel = GroupChatUserModel(loggedInUsername, userImage)
            GetGroupChats.getAllGroupChats(firestore, groupUserModel) { groupChats ->
                val oldList = homeChats.value ?: emptyList()
                val updatedList = oldList.toMutableList()
                updatedList.removeIf { it.type == HomeLayoutType.GROUP }
                updatedList.addAll(
                    groupChats.map {
                        HomeChatModel(it.id, HomeLayoutType.GROUP, null, it)
                    }
                )
                homeChats.value = updatedList
            }
        }

        fun sendTextMessage(
            chatModel: ChatModel,
            text: String,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendChat.sendTextMessage(firestore, chatModel, text, from, to, chatMessageId)
        }

        fun sendImage(
            chatModel: ChatModel,
            imageUri: Uri,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val storage = FirebaseStorage.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            StorageUtils.getUrlFromStorage(
                storage,
                "${StorageFolders.CHAT_IMAGES_FOLDER}/${chatModel.chatId}/${UUID.randomUUID()}",
                imageUri
            ) { url ->
                if (url == null) {
                    chatMessageId(null)
                    return@getUrlFromStorage
                }
                SendChat.sendImage(firestore, chatModel, url, from, to, chatMessageId)
            }
        }

        fun updateSeen(
            context: Context,
            chatModel: ChatModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(context) ?: return
            UpdateSeen.updateSeen(firestore, chatModel, loggedInUser, onSuccess)
        }
    }
}