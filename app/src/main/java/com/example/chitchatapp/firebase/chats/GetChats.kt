package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class GetChats {
    companion object {
        private val TAG = "GetChats"

        fun getAllUserChats(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (List<ChatModel>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).where(
                //compound query to get all chats where user is either user1 or user2
                Filter.or(
                    Filter.equalTo(
                        //dmChatUser1.username
                        "${ChatConstants.DM_CHAT_USER_1}.${UserConstants.USERNAME}",
                        username
                    ),
                    Filter.equalTo(
                        //dmChatUser2.username
                        "${ChatConstants.DM_CHAT_USER_2}.${UserConstants.USERNAME}",
                        username
                    )
                )
            ).addSnapshotListener { value, error ->
                if (error != null) {
                    onSuccess(listOf())
                    return@addSnapshotListener
                }

                val chats = mutableListOf<ChatModel>()
                for (doc in value!!) {
                    val chat = doc.toObject(ChatModel::class.java)
                    chats.add(chat)
                }
                onSuccess(chats)
            }
        }

        fun getUserChatById(
            firestore: FirebaseFirestore,
            chatId: String,
            onSuccess: (ChatModel?) -> Unit
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatId)
                .get()
                .addOnSuccessListener {
                    if (it == null) {
                        onSuccess(null)
                        return@addOnSuccessListener
                    }

                    val chatModel = it.toObject(ChatModel::class.java)
                    onSuccess(chatModel)
                }
        }

        fun getLiveUserChatById(
            firestore: FirebaseFirestore,
            chatId: String,
            onSuccess: (ChatModel?) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onSuccess(null)
                        return@addSnapshotListener
                    }

                    val chat = value?.toObject(ChatModel::class.java)
                    onSuccess(chat)
                }
        }
    }
}