package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.Constants
import com.example.chitchatapp.enums.ChatMessageEnums
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AddChat {
    companion object {
        fun addNewChat(
            firestore: FirebaseFirestore,
            loggedInUser: FirebaseUser?,
            newChatUser: UserModel,
            senderUser: UserModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val chatDocId = Utils.getChatDocId(loggedInUser?.uid.toString(), newChatUser.uid)

            val data = hashMapOf(
                "chats" to listOf(
                    ChatMessageModel(
                        chatDocId,
                        ChatMessageEnums.ChatMessageTypeFirstMessage,
                        "Hi, I am ${loggedInUser?.displayName.toString()}.",
                        null,
                        null,
                        null,
                        Timestamp.now(),
                        false,
                        senderUser.username,
                        newChatUser.username,
                    )
                )
            )

            firestore.collection(Constants.FIRESTORE_CHATS_COLLECTION).document(chatDocId)
                .set(data)
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}