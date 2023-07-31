package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.GroupChatsRepository
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.UserChatsRepository
import com.example.chitchatapp.store.UserStore

class GroupChatViewModel : ViewModel() {
    private val _groupChatDetails = MutableLiveData<GroupChatModel?>(null)
    val groupChatDetails: LiveData<GroupChatModel?>
        get() = _groupChatDetails

    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getGroupChatDetails(groupId: String, onSuccess: (GroupChatModel?) -> Unit) =
        GroupChatsRepository.getGroupChatById(groupId, onSuccess)


    fun getLiveGroupChatDetails(context: Context, groupId: String) {
        //always fetch latest chat details
        GroupChatsRepository.getLiveGroupChatById(groupId) {
            _groupChatDetails.value = it
        }
        UserChatsRepository.getAllUserChats(context)
    }

    fun createGroup(
        context: Context,
        groupName: String,
        groupImageUri: Uri?,
        selectedUsers: List<ChatModel>,
        onSuccess: (String?) -> Unit,
    ) {
        GroupsRepository.createGroup(
            context, groupName, groupImageUri, selectedUsers, onSuccess
        )
    }

    fun sendTextMessage(
        context: Context,
        text: String,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = _groupChatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        GroupChatsRepository.sendGroupTextMessage(
            context, groupChatModel, text, from, chatMessageId
        )
    }

    fun sendImageMessage(
        context: Context,
        imageUri: Uri,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = _groupChatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        GroupChatsRepository.sendGroupImage(context, groupChatModel, imageUri, from, chatMessageId)
    }

    fun sendSticker(
        context: Context,
        stickerIndex: Int,
        chatMessageId: (String?) -> Unit,
    ) {
        val groupChatModel = _groupChatDetails.value ?: return
        val from = getLoggedInUsername(context) ?: return
        GroupChatsRepository.sendGroupSticker(
            context, groupChatModel, stickerIndex, from, chatMessageId
        )
    }

    fun updateSeen(context: Context, onSuccess: (Boolean) -> Unit) {
        val groupChatModel = _groupChatDetails.value ?: return
        GroupChatsRepository.updateGroupSeen(context, groupChatModel, onSuccess)
    }

    fun exitGroup(context: Context, onSuccess: (Boolean) -> Unit) {
        val groupChatModel = _groupChatDetails.value ?: return
        GroupsRepository.exitGroup(context, groupChatModel, onSuccess)
    }
}