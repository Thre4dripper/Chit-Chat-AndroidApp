package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class GetChats {
    companion object {
        private val TAG = "GetChats"

        fun getAllUserChats(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (List<ChatModel>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).where(
                Filter.or(
                    Filter.equalTo(ChatConstants.CHAT_USERNAME_1, username),
                    Filter.equalTo(ChatConstants.CHAT_USERNAME_2, username)
                )
            ).addSnapshotListener { value, error ->
                if (error != null) {
                    onSuccess(listOf())
                    return@addSnapshotListener
                }

                val chats = mutableListOf<ChatModel>()
                for (doc in value!!) {
                    val chat = doc.toObject(ChatModel::class.java)
                    chats.add(chat)
                }

                val sortedChats =
                    chats.sortedByDescending { it.chatMessages.last().chatMessageTime }
                onSuccess(sortedChats)
            }
        }
    }
}