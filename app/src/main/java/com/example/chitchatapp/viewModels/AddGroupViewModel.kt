package com.example.chitchatapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.repository.ChatsRepository

class AddGroupViewModel : ViewModel() {

    fun searchUsers(searchQuery: String, loggedInUsername: String): LiveData<List<ChatModel>?> {
        return Transformations.map(ChatsRepository.homeChats) { homeChats ->
            homeChats?.filter {
                //chat name which is displayed in the results list
                val chatName = ChatUtils.getChatUsername(it, loggedInUsername)

                //filtering logic
                //searching in both dmChatUser1 and dmChatUser2 usernames
                //this will return true if any of the username contains searchQuery
                //even if one of the username is loggedInUsername
                //it will return that result also, but displayed chatName will be other username
                //so we will also check if displayed chatName contains searchQuery
                val filter = (it.dmChatUser1.username.contains(searchQuery, true) ||
                        it.dmChatUser2.username.contains(searchQuery, true)) &&

                        //by this we will eliminate the case where one of the username is loggedInUsername
                        chatName.contains(searchQuery, true)

                filter
            }
        }
    }
}