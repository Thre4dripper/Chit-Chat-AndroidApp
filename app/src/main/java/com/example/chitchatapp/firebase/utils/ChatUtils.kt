package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.Constants
import com.google.firebase.firestore.FirebaseFirestore

class ChatUtils {
    companion object {
        fun getChatDocId(
            chatUserId1: String,
            chatUserId2: String,
        ): String {
            return if (chatUserId1 < chatUserId2)
                "$chatUserId1-$chatUserId2"
            else
                "$chatUserId2-$chatUserId1"
        }

        fun checkIfChatExists(
            firestore: FirebaseFirestore,
            chatUserId1: String,
            chatUserId2: String,
            callback: (Boolean) -> Unit,
        ) {
            val chatDocId = getChatDocId(chatUserId1, chatUserId2)
            firestore.collection(Constants.FIRESTORE_CHATS_COLLECTION)
                .document(chatDocId)
                .get()
                .addOnSuccessListener {
                    callback(it.exists())
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }
}