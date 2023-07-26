package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore

class MarkFavourite {
    companion object {
        fun markAsFavourite(
            firestore: FirebaseFirestore,
            chatId: String,
            favourite: Boolean,
            onSuccess: (Boolean?) -> Unit
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatId)
                .update(ChatConstants.FAVOURITE, favourite)
                .addOnSuccessListener {
                    onSuccess(favourite)
                }
                .addOnFailureListener {
                    onSuccess(null)
                }
        }
    }
}