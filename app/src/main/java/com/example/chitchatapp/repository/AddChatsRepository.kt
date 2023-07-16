package com.example.chitchatapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.chats.AddChat
import com.example.chitchatapp.firebase.user.FirestoreSearchUsers
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
            chatUserId: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = FirebaseAuth.getInstance().currentUser

            val currentUser = UserDetailsRepository.userDetails.value
            AddChat.addNewChat(
                firestore,
                loggedInUser,
                chatUserId,
                currentUser!!.username,
                onSuccess
            )
        }
    }
}