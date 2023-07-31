package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun updateGroupSeen(
            firestore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            loggedInUser: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            val oldMessagesList = groupChatModel.messages
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
            val updatedChatModel = groupChatModel.copy(
                messages = newMessagesList
            )

            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}