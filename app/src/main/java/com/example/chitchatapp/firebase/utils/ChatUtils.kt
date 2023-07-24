package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
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
            chatId: (String?) -> Unit,
        ) {
            val chatDocId = getDMChatDocId(chatUserId1, chatUserId2)
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatDocId)
                .get()
                .addOnSuccessListener {
                    if (it.exists())
                        chatId(it.id)
                    else
                        chatId(null)
                }
                .addOnFailureListener {
                    chatId(null)
                }
        }

        fun getChatProfileImage(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.profileImage
            } else {
                chatModel.dmChatUser1.profileImage
            }
        }

        fun getChatUsername(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.username
            } else {
                chatModel.dmChatUser1.username
            }
        }

        fun getChatStatus(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.status
            } else {
                chatModel.dmChatUser1.status
            }
        }

        fun getCurrentUserImage(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser1.profileImage
            } else {
                chatModel.dmChatUser2.profileImage
            }
        }

        fun isStatusChanged(oldChatDetails: ChatModel?, newChatDetails: ChatModel?): Boolean {
            return oldChatDetails?.dmChatUser1?.status != newChatDetails?.dmChatUser1?.status
                    || oldChatDetails?.dmChatUser2?.status != newChatDetails?.dmChatUser2?.status
        }
    }
}