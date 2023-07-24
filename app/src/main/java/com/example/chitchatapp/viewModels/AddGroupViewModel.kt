package com.example.chitchatapp.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.ChatsRepository

class AddGroupViewModel : ViewModel() {
    private var _searchedUsers = MutableLiveData<List<ChatModel>>()
    val searchedUsers: LiveData<List<ChatModel>?>
        get() = _searchedUsers

    private var _selectedUsers = MutableLiveData<List<ChatModel>>(mutableListOf())
    val selectedUsers: LiveData<List<ChatModel>?>
        get() = _selectedUsers

    fun searchUsers(searchQuery: String, loggedInUsername: String) {
        //updating livedata
        _searchedUsers.value =
            ChatsRepository.homeChats.value?.filter { homeChat ->
                //chat name which is displayed in the results list
                val chatName = ChatUtils.getChatUsername(homeChat, loggedInUsername)

                /** filtering logic
                 * searching in both dmChatUser1 and dmChatUser2 usernames
                 * this will return true if any of the username contains searchQuery
                 * even if one of the username is loggedInUsername
                 * it will return that result also, but displayed chatName will be other username
                 * so we will also check if displayed chatName contains searchQuery
                 */
                val filter = (homeChat.dmChatUser1.username.contains(searchQuery, true) ||
                        homeChat.dmChatUser2.username.contains(searchQuery, true)) &&

                        //by this we will eliminate the case where one of the username is loggedInUsername
                        chatName.contains(searchQuery, true)

                filter
            }
    }

    fun addSelectedUser(chatModel: ChatModel) {
        val oldList = _selectedUsers.value
        val updatedList = oldList?.toMutableList()
        updatedList?.add(chatModel)
        _selectedUsers.value = updatedList
    }

    fun removeSelectedUser(chatModel: ChatModel) {
        val oldList = _selectedUsers.value
        val updatedList = oldList?.toMutableList()
        updatedList?.remove(chatModel)
        _selectedUsers.value = updatedList
    }

    fun createGroup(
        context: Context,
        groupName: String,
        groupImageUri: Uri?,
        onSuccess: (Boolean) -> Unit,
    ) {
        val selectedUsers = _selectedUsers.value

        if (selectedUsers == null) {
            onSuccess(false)
            return
        }
        GroupsRepository.createGroup(
            context,
            groupName,
            groupImageUri,
            selectedUsers,
            onSuccess
        )
    }
}