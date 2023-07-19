package com.example.chitchatapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.chats.GetAllChats
import com.example.chitchatapp.firebase.chats.SendChat
import com.example.chitchatapp.firebase.chats.UpdateSeen
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
            GetAllChats.getAllUserChats(firestore, loggedInUser) {
                homeChats.value = it
            }
        }

        fun sendTextMessage(
            chatModel: ChatModel,
            text: String,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            SendChat.sendTextMessage(firestore, chatModel, text, from, to, chatMessageId)
        }

        fun updateSeen(
            context: Context,
            chatModel: ChatModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserDetails.getUsername(context) ?: return
            UpdateSeen.updateSeen(firestore, chatModel, loggedInUser, onSuccess)
        }
    }
}