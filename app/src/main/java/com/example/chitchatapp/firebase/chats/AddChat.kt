package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.DMChatUserModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddChat {
    companion object {
        fun addNewChat(
            firestore: FirebaseFirestore,
            loggedInUser: FirebaseUser?,
            newChatUser: UserModel,
            currentUser: UserModel,
            chatId: (String?) -> Unit,
        ) {
            val chatDocId = ChatUtils.getDMChatDocId(loggedInUser?.uid.toString(), newChatUser.uid)
            val data = ChatModel(
                chatDocId,
                DMChatUserModel(
                    currentUser.username,
                    currentUser.profileImage
                ),
                DMChatUserModel(
                    newChatUser.username,
                    newChatUser.profileImage
                ),
                false,
                listOf(
                    ChatMessageModel(
                        UUID.randomUUID().toString(),
                        ChatMessageType.TypeFirstMessage,
                        null,
                        null,
                        null,
                        null,
                        Timestamp.now(),
                        listOf(currentUser.username),
                        currentUser.username,
                        newChatUser.username,
                    ),
                    ChatMessageModel(
                        UUID.randomUUID().toString(),
                        ChatMessageType.TypeMessage,
                        "Hi, I am ${loggedInUser?.displayName.toString()}.",
                        null,
                        null,
                        null,
                        Timestamp.now(),
                        listOf(currentUser.username),
                        currentUser.username,
                        newChatUser.username,
                    )
                )
            )

            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatDocId)
                .set(data)
                .addOnSuccessListener {
                    chatId(chatDocId)
                }
                .addOnFailureListener {
                    chatId(null)
                }
        }
    }
}