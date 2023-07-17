package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.Constants
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore

class GetChats {
    companion object {
        private val TAG = "GetChats"

        fun getAllUserChats(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (List<ChatModel>) -> Unit,
        ) {
            firestore.collection(Constants.FIRESTORE_CHATS_COLLECTION)
                .get()
                .addOnSuccessListener { result ->
                    val chats = mutableListOf<ChatModel>()
                    for (document in result) {
                        val chat = document.toObject(ChatModel::class.java)
                        chats.add(chat)
                    }
                    onSuccess(chats)
                }
                .addOnFailureListener {
                    onSuccess(listOf())
                }
        }
    }
}