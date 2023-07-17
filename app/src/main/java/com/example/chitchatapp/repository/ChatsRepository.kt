package com.example.chitchatapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.chats.GetChats
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.store.UserDetails
import com.google.firebase.firestore.FirebaseFirestore

class ChatsRepository {
    companion object {
        val homeChats = MutableLiveData<List<ChatModel>?>(null)

        fun getAllUserChats(
            context: Context
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserDetails.getUsername(context) ?: ""
            GetChats.getAllUserChats(firestore, loggedInUser) {
                homeChats.value = it
            }
        }
    }
}