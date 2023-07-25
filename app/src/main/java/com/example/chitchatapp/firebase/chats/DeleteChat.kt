package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore

class DeleteChat {
    companion object {
        fun deleteUserChat(
            firestore: FirebaseFirestore, chatModel: ChatModel, onSuccess: (Boolean) -> Unit
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId).delete().addOnSuccessListener {
                    onSuccess(true)
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}