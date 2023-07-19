package com.example.chitchatapp.firebase.utils

import android.text.format.DateUtils
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.Timestamp
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

        fun getChatTime(timestamp: Timestamp): String {
            val date = timestamp.toDate()
            val time = DateUtils.getRelativeTimeSpanString(
                date.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )

            return time.toString()
        }
    }
}