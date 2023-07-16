package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.Constants
import com.example.chitchatapp.enums.ChatMessageEnums
import com.example.chitchatapp.models.ChatMessageModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AddChat {
    companion object {
        fun addNewChat(
            firestore: FirebaseFirestore,
            loggedInUser: FirebaseUser?,
            chatUserId: String,
            senderUsername: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            val chatDocId = if (loggedInUser?.uid.toString() < chatUserId)
                loggedInUser?.uid.toString() + "-" + chatUserId
            else
                chatUserId + "-" + loggedInUser?.uid.toString()

            val chatMessage = ChatMessageModel(
                chatDocId,
                ChatMessageEnums.ChatMessageTypeFirstMessage,
                "Hi, I am ${loggedInUser?.displayName.toString()}.",
                null,
                null,
                null,
                Date().time.toString(),
                false,
                senderUsername
            )

            firestore.collection(Constants.FIRESTORE_CHATS_COLLECTION).document(chatDocId)
                .set(chatMessage)
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}