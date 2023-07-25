package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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

        fun deleteChatImages(
            storage: FirebaseStorage, chatModel: ChatModel, onSuccess: (Boolean) -> Unit
        ) {
            val ref =
                storage.reference.child("${StorageFolders.CHAT_IMAGES_FOLDER}/${chatModel.chatId}")
            ref.delete().addOnSuccessListener {
                onSuccess(true)
            }.addOnFailureListener {
                onSuccess(false)
            }
        }
    }
}