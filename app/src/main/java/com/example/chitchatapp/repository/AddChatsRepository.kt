package com.example.chitchatapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.chats.AddChat
import com.example.chitchatapp.firebase.user.FirestoreSearchUsers
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddChatsRepository {
    companion object {
        val searchResult = MutableLiveData<ArrayList<UserModel>>()

        fun searchUsers(
            searchQuery: String,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = FirebaseAuth.getInstance().currentUser

            FirestoreSearchUsers.searchUsers(firestore, loggedInUser, searchQuery) {
                searchResult.value = it as ArrayList<UserModel>
            }
        }

        fun addChat(
            newChatUser: UserModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = FirebaseAuth.getInstance().currentUser
            val currentUser = UserDetailsRepository.userDetails.value

            // Check if chat already exists
            ChatUtils.checkIfChatExists(
                firestore, loggedInUser!!.uid, newChatUser.uid
            ) { isExist ->
                if (isExist) {
                    onSuccess(true)
                    return@checkIfChatExists
                }
                //otherwise add new chat
                AddChat.addNewChat(firestore, loggedInUser, newChatUser, currentUser!!) { newChat ->
                    if (newChat == null) return@addNewChat

                    //add new chat to homeChats
                    val oldHomeChats = HomeRepository.homeChats.value?.toMutableList()
                    val newChatsList = oldHomeChats?.toMutableList() ?: mutableListOf()
                    newChatsList.add(newChat)
                    HomeRepository.homeChats.value = newChatsList
                    onSuccess(true)
                }
            }
        }
    }
}