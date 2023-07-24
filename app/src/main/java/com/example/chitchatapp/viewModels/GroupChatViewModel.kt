package com.example.chitchatapp.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.ChatGroupModel
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.store.UserStore

class GroupChatViewModel : ViewModel() {
    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }

    fun getGroupChatDetails(groupId: String): ChatGroupModel? {
        return ChatsRepository.homeChats.value?.find { homeChat ->
            homeChat.id == groupId
        }?.groupChat
    }

    fun getLiveGroupChatDetails(groupId: String): LiveData<ChatGroupModel?> {
        return Transformations.map(ChatsRepository.homeChats) { homeChats ->
            homeChats?.find { homeChat ->
                homeChat.id == groupId
            }?.groupChat
        }
    }
}