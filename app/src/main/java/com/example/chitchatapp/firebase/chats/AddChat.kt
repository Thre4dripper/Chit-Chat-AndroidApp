package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
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
            val chatDocId = ChatUtils.getChatDocId(loggedInUser?.uid.toString(), newChatUser.uid)
            val data = ChatModel(
                chatDocId,
                senderUser.username,
                newChatUser.username,
                senderUser.profileImage,
                newChatUser.profileImage,
                false,
                listOf(
                    ChatMessageModel(
                        chatDocId,
                        ChatMessageType.TypeFirstMessage,
                        "Hi, I am ${loggedInUser?.displayName.toString()}.",
                        null,
                        null,
                        null,
                        Timestamp.now(),
                        listOf(senderUser.username),
                        senderUser.username,
                        newChatUser.username,
                    )
                )
            )

            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatDocId)
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