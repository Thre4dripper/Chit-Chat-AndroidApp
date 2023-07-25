package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ClearChat {
    companion object {
        fun clearUserChat(
            firestore: FirebaseFirestore, chatModel: ChatModel, onSuccess: (Boolean) -> Unit
        ) {
            //deleting all messages except the first one
            val newChatModel = chatModel.copy(
                chatMessages = listOf(chatModel.chatMessages.first())
            )
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(newChatModel.chatId).set(newChatModel).addOnSuccessListener {
                    onSuccess(true)
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun clearChatImages(
            storage: FirebaseStorage, chatModel: ChatModel, onSuccess: (Boolean) -> Unit
        ) {
            //list all files in chat folder
            storage.reference.child("${StorageFolders.CHAT_IMAGES_FOLDER}/${chatModel.chatId}/")
                .listAll()
                .addOnSuccessListener { listResult ->
                    //delete all files in chat folder
                    listResult.items.forEach { item ->
                        item.delete()
                    }

                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}