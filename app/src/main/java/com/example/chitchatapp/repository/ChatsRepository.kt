package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.firebase.chats.GetAllChats
import com.example.chitchatapp.firebase.chats.SendChat
import com.example.chitchatapp.firebase.chats.UpdateSeen
import com.example.chitchatapp.firebase.user.UpdateStatus
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ChatsRepository {
    companion object {
        private val TAG = "ChatsRepository"

        val homeChats = MutableLiveData<List<ChatModel>?>(null)

        fun getAllUserChats(
            context: Context
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(context) ?: ""
            GetAllChats.getAllUserChats(firestore, loggedInUser) {
                homeChats.value = it
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
                " ${StorageFolders.CHAT_IMAGES_FOLDER}/${chatModel.chatId}/${UUID.randomUUID()}",
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

        fun updateUserStatus(
            chatModel: ChatModel, loggedInUser: String, status: String, onSuccess: (String?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            UpdateStatus.updateDMUserStatus(
                firestore, chatModel, loggedInUser, status, onSuccess
            )
        }
    }
}