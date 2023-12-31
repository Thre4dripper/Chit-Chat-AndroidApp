package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.google.firebase.firestore.FirebaseFirestore

class ChatUtils {
    companion object {
        fun getUserChatDocId(
            chatUserId1: String,
            chatUserId2: String,
        ): String {
            return if (chatUserId1 < chatUserId2)
                "$chatUserId1-$chatUserId2"
            else
                "$chatUserId2-$chatUserId1"
        }

        fun checkIfUserChatExists(
            firestore: FirebaseFirestore,
            chatUserId1: String,
            chatUserId2: String,
            chatId: (String?) -> Unit,
        ) {
            val chatDocId = getUserChatDocId(chatUserId1, chatUserId2)
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

        fun getUserChatProfileImage(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.profileImage
            } else {
                chatModel.dmChatUser1.profileImage
            }
        }

        fun getUserChatUsername(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.username
            } else {
                chatModel.dmChatUser1.username
            }
        }

        fun getUserChatStatus(chatModel: ChatModel, loggedInUsername: String): String {
            return if (chatModel.dmChatUser1.username == loggedInUsername) {
                chatModel.dmChatUser2.status
            } else {
                chatModel.dmChatUser1.status
            }
        }

        fun getGroupChatProfileImage(
            groupChatModel: GroupChatModel,
            from: String
        ): String {
            return groupChatModel.members.find {
                it.username == from
            }?.profileImage ?: ""
        }
    }
}