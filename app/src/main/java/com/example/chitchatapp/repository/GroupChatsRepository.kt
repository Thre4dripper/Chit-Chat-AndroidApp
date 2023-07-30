package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.chats.GetGroupChats
import com.example.chitchatapp.firebase.chats.SendGroupChat
import com.example.chitchatapp.firebase.chats.UpdateSeen
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.models.HomeChatModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class GroupChatsRepository {
    companion object {
        fun getAllGroupChats(context: Context) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: ""
            val userImage = UserRepository.userDetails.value?.profileImage ?: ""

            val groupUserModel = GroupChatUserModel(loggedInUsername, userImage)
            GetGroupChats.getAllGroupChats(firestore, groupUserModel) { groupChats ->
                val oldList = HomeRepository.homeChats.value ?: emptyList()
                val updatedList = oldList.toMutableList()
                updatedList.removeIf { it.type == ChatType.GROUP }
                updatedList.addAll(groupChats.map {
                    HomeChatModel(
                        it.id, ChatType.GROUP, null, it, it.messages.first().time
                    )
                })

                val sortedList = updatedList.sortedByDescending { it.lastMessageTimestamp }
                HomeRepository.homeChats.value = sortedList
            }
        }

        fun getGroupChatById(
            chatId: String,
            chatModel: (GroupChatModel?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            GetGroupChats.getGroupChatById(firestore, chatId, chatModel)
        }

        fun sendGroupTextMessage(
            context: Context,
            groupChatModel: GroupChatModel,
            text: String,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendGroupChat.sendTextMessage(
                context, firestore, groupChatModel, text, from, chatMessageId
            )
        }

        fun sendGroupImage(
            context: Context,
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
                SendGroupChat.sendImage(
                    context, firestore, groupChatModel, url, from, chatMessageId
                )
            }
        }

        fun sendGroupSticker(
            context: Context,
            groupChatModel: GroupChatModel,
            stickerIndex: Int,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendGroupChat.sendSticker(
                context, firestore, groupChatModel, stickerIndex, from, chatMessageId
            )
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