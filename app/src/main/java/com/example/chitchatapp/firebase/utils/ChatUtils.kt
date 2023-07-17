package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.constants.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore

class ChatUtils {
    companion object {
        fun getDMChatDocId(
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
            val chatDocId = getDMChatDocId(chatUserId1, chatUserId2)
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
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