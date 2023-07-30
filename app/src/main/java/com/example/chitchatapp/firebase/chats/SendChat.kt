package com.example.chitchatapp.firebase.chats

import android.content.Context
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.messaging.user.ImageNotification
import com.example.chitchatapp.firebase.messaging.user.StickerNotification
import com.example.chitchatapp.firebase.messaging.user.TextNotification
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.UUID

class SendChat {
    companion object {
        fun sendTextMessage(
            context: Context,
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
                0,
                ChatMessageModel(
                    id,
                    ChatMessageType.TypeText,
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
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge()).addOnSuccessListener {
                    if (!chatModel.mutedBy.contains(to)) {
                        TextNotification.sendTextNotification(
                            context, firestore, chatModel.chatId, text, from, to
                        )
                    }
                    chatMessageId(id)
                }.addOnFailureListener {
                    chatMessageId(null)
                }
        }

        fun sendImage(
            context: Context,
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
                0,
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
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge()).addOnSuccessListener {

                    if (!chatModel.mutedBy.contains(to)) {
                        ImageNotification.sendImageNotification(
                            context, firestore, chatModel.chatId, imageUrl, from, to
                        )
                    }
                    chatMessageId(id)
                }.addOnFailureListener {
                    chatMessageId(null)
                }
        }

        fun sendSticker(
            context: Context,
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
                0,
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
            firestore.collection(FirestoreCollections.CHATS_COLLECTION).document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge()).addOnSuccessListener {
                    if (!chatModel.mutedBy.contains(to)) {
                        StickerNotification.sendStickerNotification(
                            context, firestore, chatModel.chatId, from, to
                        )
                    }
                    chatMessageId(id)
                }.addOnFailureListener {
                    chatMessageId(null)
                }
        }
    }
}