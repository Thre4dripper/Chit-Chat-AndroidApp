package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore

class UpdateSeen {
    companion object {
        fun updateSeen(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            loggedInUser: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            val existingMessages = chatModel.chatMessages
            val newList = existingMessages.toMutableList()
            for (i in newList.indices) {
                val message = newList[i]
                if (message.chatMessageFrom != loggedInUser) {
                    val seenBy = message.seenBy.toMutableList()
                    if (!seenBy.contains(loggedInUser)) {
                        seenBy.add(loggedInUser)
                    }
                    newList[i] = message.copy(
                        seenBy = seenBy
                    )
                }
            }
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = chatModel.copy(
                chatMessages = newList
            )

            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
                .set(updatedChatModel)
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}