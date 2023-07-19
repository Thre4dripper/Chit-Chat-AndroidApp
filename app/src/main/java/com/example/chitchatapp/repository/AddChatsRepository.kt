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
            chatId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = FirebaseAuth.getInstance().currentUser
            val currentUser = UserDetailsRepository.userDetails.value

            // Check if chat already exists
            ChatUtils.checkIfChatExists(
                firestore, loggedInUser!!.uid, newChatUser.uid
            ) {
                //if chat exists, return chatId
                if (it != null) {
                    chatId(it)
                    return@checkIfChatExists
                }
                //otherwise add new chat
                AddChat.addNewChat(firestore, loggedInUser, newChatUser, currentUser!!, chatId)
            }
        }
    }
}