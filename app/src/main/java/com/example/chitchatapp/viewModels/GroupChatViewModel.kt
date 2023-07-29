package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.repository.GroupChatsRepository
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.HomeRepository
import com.example.chitchatapp.store.UserStore

class GroupChatViewModel : ViewModel() {
    private val _groupChatDetails = MutableLiveData<GroupChatModel?>(null)
    val groupChatDetails: LiveData<GroupChatModel?>
        get() = _groupChatDetails

    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getLiveGroupChatDetails(lifecycleOwner: LifecycleOwner, groupId: String) {
        //when chat is already loaded in home fragment then take it from there
        if (HomeRepository.homeChats.value != null) {
            HomeRepository.homeChats.observe(lifecycleOwner) {
                val groupChatModel =
                    it?.find { chat -> chat.groupChat?.id == groupId }?.groupChat
                _groupChatDetails.value = groupChatModel
            }
        }
        //otherwise load it from firebase (open from notification case)
        else {
            GroupChatsRepository.getGroupChatById(groupId) {
                _groupChatDetails.value = it
            }
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