package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupMessageModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.UUID

class SendGroupChat {
    companion object {
        fun sendTextMessage(
            firestore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            text: String,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val id = UUID.randomUUID().toString()
            val oldMessagesList = groupChatModel.messages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                GroupMessageModel(
                    id,
                    GroupMessageType.TypeMessage,
                    text,
                    null,
                    null,
                    Timestamp.now(),
                    listOf(from),
                    from
                )
            )
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = groupChatModel.copy(
                messages = newMessagesList
            )
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    chatMessageId(id)
                }
                .addOnFailureListener {
                    chatMessageId(null)
                }
        }

        fun sendImage(
            firestore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            imageUrl: String,
            from: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val id = UUID.randomUUID().toString()
            val oldMessagesList = groupChatModel.messages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                GroupMessageModel(
                    id,
                    GroupMessageType.TypeImage,
                    null,
                    imageUrl,
                    null,
                    Timestamp.now(),
                    listOf(from),
                    from,
                )
            )
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = groupChatModel.copy(
                messages = newMessagesList
            )
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    chatMessageId(id)
                }
                .addOnFailureListener {
                    chatMessageId(null)
                }
        }
    }
}