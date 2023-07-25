package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.ChatModel
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
        return Transformations.map(ChatsRepository.homeChats) { homeChats ->
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
}