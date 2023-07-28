package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.UUID

class SendChat {
    companion object {
        fun sendTextMessage(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            text: String,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val id = UUID.randomUUID().toString()
            val oldMessagesList = chatModel.chatMessages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                ChatMessageModel(
                    id,
                    ChatMessageType.TypeMessage,
                    text,
                    null,
                    null,
                    Timestamp.now(),
                    listOf(from),
                    from,
                    to,
                )
            )
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = chatModel.copy(
                chatMessages = newMessagesList
            )
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
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
            chatModel: ChatModel,
            imageUrl: String,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val id = UUID.randomUUID().toString()
            val oldMessagesList = chatModel.chatMessages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                ChatMessageModel(
                    id,
                    ChatMessageType.TypeImage,
                    null,
                    imageUrl,
                    null,
                    Timestamp.now(),
                    listOf(from),
                    from,
                    to,
                )
            )
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = chatModel.copy(
                chatMessages = newMessagesList
            )
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    chatMessageId(id)
                }
                .addOnFailureListener {
                    chatMessageId(null)
                }
        }

        fun sendSticker(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            stickerIndex: Int,
            from: String,
            to: String,
            chatMessageId: (String?) -> Unit,
        ) {
            val id = UUID.randomUUID().toString()
            val oldMessagesList = chatModel.chatMessages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                ChatMessageModel(
                    id,
                    ChatMessageType.TypeSticker,
                    null,
                    null,
                    stickerIndex,
                    Timestamp.now(),
                    listOf(from),
                    from,
                    to,
                )
            )
            //no worry, firestore will merge the data, and only update the chatMessages field
            val updatedChatModel = chatModel.copy(
                chatMessages = newMessagesList
            )
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
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