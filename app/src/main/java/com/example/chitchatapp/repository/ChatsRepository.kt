package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.chats.ClearChat
import com.example.chitchatapp.firebase.chats.DeleteChat
import com.example.chitchatapp.firebase.chats.GetChats
import com.example.chitchatapp.firebase.chats.GetGroupChats
import com.example.chitchatapp.firebase.chats.MarkFavourite
import com.example.chitchatapp.firebase.chats.SendChat
import com.example.chitchatapp.firebase.chats.SendGroupChat
import com.example.chitchatapp.firebase.chats.UpdateSeen
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.models.HomeChatModel
import com.example.chitchatapp.models.UserModel
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
                updatedList.removeIf { it.type == ChatType.USER }
                updatedList.addAll(
                    userChats.map {
                        HomeChatModel(
                            it.chatId,
                            ChatType.USER,
                            it,
                            null,
                            it.chatMessages.last().time
                        )
                    }
                )

                val sortedList = updatedList.sortedByDescending { it.lastMessageTimestamp }
                homeChats.value = sortedList
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
                updatedList.removeIf { it.type == ChatType.GROUP }
                updatedList.addAll(
                    groupChats.map {
                        HomeChatModel(
                            it.id,
                            ChatType.GROUP,
                            null,
                            it,
                            it.messages.last().time
                        )
                    }
                )

                val sortedList = updatedList.sortedByDescending { it.lastMessageTimestamp }
                homeChats.value = sortedList
            }
        }

        fun sendTextMessage(
            context: Context,
            chatModel: ChatModel,
            text: String,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendChat.sendTextMessage(context, firestore, chatModel, text, from, to, chatMessageId)
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

        fun sendSticker(
            chatModel: ChatModel,
            stickerIndex: Int,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendChat.sendSticker(firestore, chatModel, stickerIndex, from, to, chatMessageId)
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

        fun favouriteChat(
            userModel: UserModel,
            favourite: String,
            onSuccess: (Boolean?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            MarkFavourite.markAsFavourite(firestore, userModel, favourite, onSuccess)
        }

        fun clearChat(chatModel: ChatModel, onSuccess: (Boolean) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            ClearChat.clearUserChat(firestore, chatModel) { isChatDeleted ->
                if (!isChatDeleted) {
                    onSuccess(false)
                    return@clearUserChat
                }
                val hasImages = chatModel.chatMessages.find { it.type == ChatMessageType.TypeImage }
                if (hasImages != null) {
                    ClearChat.clearChatImages(storage, chatModel) { isImagesDeleted ->
                        onSuccess(isImagesDeleted)
                    }
                } else {
                    onSuccess(true)
                }
            }
        }

        fun deleteChat(chatModel: ChatModel, onSuccess: (Boolean) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            DeleteChat.deleteUserChat(firestore, chatModel) { isChatDeleted ->
                if (!isChatDeleted) {
                    onSuccess(false)
                    return@deleteUserChat
                }
                val hasImages = chatModel.chatMessages.any {
                    it.type == ChatMessageType.TypeImage
                }
                if (!hasImages) {
                    onSuccess(true)
                    return@deleteUserChat
                }
                ClearChat.clearChatImages(storage, chatModel) { isImagesDeleted ->
                    onSuccess(isImagesDeleted)
                }
            }
        }


        fun sendGroupTextMessage(
            groupChatModel: GroupChatModel,
            text: String,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendGroupChat.sendTextMessage(firestore, groupChatModel, text, from, chatMessageId)
        }

        fun sendGroupImage(
            groupChatModel: GroupChatModel,
            imageUri: Uri,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val storage = FirebaseStorage.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            StorageUtils.getUrlFromStorage(
                storage,
                "${StorageFolders.CHAT_IMAGES_FOLDER}/${groupChatModel.id}/${UUID.randomUUID()}",
                imageUri
            ) { url ->
                if (url == null) {
                    chatMessageId(null)
                    return@getUrlFromStorage
                }
                SendGroupChat.sendImage(firestore, groupChatModel, url, from, chatMessageId)
            }
        }

        fun sendGroupSticker(
            groupChatModel: GroupChatModel,
            stickerIndex: Int,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendGroupChat.sendSticker(firestore, groupChatModel, stickerIndex, from, chatMessageId)
        }

        fun updateGroupSeen(
            context: Context,
            groupChatModel: GroupChatModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(context) ?: return
            UpdateSeen.updateGroupSeen(firestore, groupChatModel, loggedInUser, onSuccess)
        }
    }
}