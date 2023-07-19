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
            val oldMessagesList = chatModel.chatMessages
            val newMessagesList = oldMessagesList.toMutableList()
            for (i in newMessagesList.indices) {
                val message = newMessagesList[i]
                if (message.from != loggedInUser) {
                    val seenBy = message.seenBy.toMutableList()
                    if (!seenBy.contains(loggedInUser)) {
                        seenBy.add(loggedInUser)
                    }
                    newMessagesList[i] = message.copy(
                        seenBy = seenBy
                    )
                }
            }
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = chatModel.copy(
                chatMessages = newMessagesList
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