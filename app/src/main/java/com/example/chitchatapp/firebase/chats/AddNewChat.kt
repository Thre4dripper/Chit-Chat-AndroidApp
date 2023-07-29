package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.DMChatUserModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddNewChat {
    companion object {
        fun addNewChat(
            firestore: FirebaseFirestore,
            newChatUser: UserModel,
            currentUser: UserModel,
            chatId: (String?) -> Unit,
        ) {
            val chatDocId = ChatUtils.getUserChatDocId(currentUser.uid, newChatUser.uid)
            val data = ChatModel(
                chatDocId,
                DMChatUserModel(
                    currentUser.username,
                    currentUser.profileImage,
                    UserStatus.Online.name
                ),
                DMChatUserModel(
                    newChatUser.username,
                    newChatUser.profileImage,
                    "${UserStatus.LastSeen.name} ${Timestamp.now().seconds}"
                ),
                listOf(
                    ChatMessageModel(
                        UUID.randomUUID().toString(),
                        ChatMessageType.TypeFirstMessage,
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
                        ChatMessageType.TypeText,
                        "Hi, I am ${currentUser.username}.",
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