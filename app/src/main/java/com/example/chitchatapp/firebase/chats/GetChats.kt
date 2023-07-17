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
            ).get().addOnSuccessListener { result ->
                val chats = mutableListOf<ChatModel>()
                for (document in result) {
                    val chat = document.toObject(ChatModel::class.java)
                    chats.add(chat)
                }
                onSuccess(chats)
            }.addOnFailureListener {
                onSuccess(listOf())
            }
        }
    }
}