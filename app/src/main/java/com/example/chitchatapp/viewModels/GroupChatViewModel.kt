package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.store.UserStore

class GroupChatViewModel : ViewModel() {
    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getGroupChatDetails(groupId: String): GroupChatModel? {
        return ChatsRepository.homeChats.value?.find { homeChat ->
            homeChat.id == groupId
        }?.groupChat
    }

    fun getLiveGroupChatDetails(groupId: String): LiveData<GroupChatModel?> {
        return ChatsRepository.homeChats.map { homeChats ->
            homeChats?.find { homeChat ->
                homeChat.id == groupId
            }?.groupChat
        }
    }

    fun createGroup(
        context: Context,
        groupName: String,
        groupImageUri: Uri?,
        selectedUsers: List<ChatModel>,
        onSuccess: (String?) -> Unit,
    ) {
        GroupsRepository.createGroup(
            context,
            groupName,
            groupImageUri,
            selectedUsers,
            onSuccess
        )
    }

    fun sendTextMessage(
        context: Context,
        chatId: String,
        text: String,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = getGroupChatDetails(chatId) ?: return
        val from = getLoggedInUsername(context) ?: return
        ChatsRepository.sendGroupTextMessage(context, groupChatModel, text, from, chatMessageId)
    }

    fun sendImageMessage(
        context: Context,
        chatId: String,
        imageUri: Uri,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = getGroupChatDetails(chatId) ?: return
        val from = getLoggedInUsername(context) ?: return
        ChatsRepository.sendGroupImage(groupChatModel, imageUri, from, chatMessageId)
    }

    fun sendSticker(
        context: Context,
        chatId: String,
        stickerIndex: Int,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = getGroupChatDetails(chatId) ?: return
        val from = getLoggedInUsername(context) ?: return
        ChatsRepository.sendGroupSticker(groupChatModel, stickerIndex, from, chatMessageId)
    }

    fun updateSeen(context: Context, groupId: String, onSuccess: (Boolean) -> Unit) {
        val groupChatModel = getGroupChatDetails(groupId) ?: return
        ChatsRepository.updateGroupSeen(context, groupChatModel, onSuccess)
    }

    fun exitGroup(context: Context, groupId: String, onSuccess: (Boolean) -> Unit) {
        val groupChatModel = getGroupChatDetails(groupId) ?: return
        GroupsRepository.exitGroup(context, groupChatModel, onSuccess)
    }
}