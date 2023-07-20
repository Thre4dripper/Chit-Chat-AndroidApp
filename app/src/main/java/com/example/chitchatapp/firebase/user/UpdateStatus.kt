package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateStatus {
    companion object {
        private val TAG = "UpdateStatus"

        fun updateDMUserStatus(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            loggedInUser: String,
            status: String,
            onSuccess: (String?) -> Unit
        ) {
            val updatedChatModel = chatModel.copy(
                dmChatUser1 = chatModel.dmChatUser1.copy(
                    status = if (chatModel.dmChatUser1.username == loggedInUser) {
                        status
                    } else {
                        chatModel.dmChatUser1.status
                    }
                ),
                dmChatUser2 = chatModel.dmChatUser2.copy(
                    status = if (chatModel.dmChatUser2.username == loggedInUser) {
                        status
                    } else {
                        chatModel.dmChatUser2.status
                    }
                )
            )

            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(status)
                }
                .addOnFailureListener {
                    onSuccess(null)
                }
        }
    }
}