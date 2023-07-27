package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DeleteChat {
    companion object {
        fun deleteUserChat(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            onSuccess: (Boolean) -> Unit
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId).delete().addOnSuccessListener {
                    val user1 = chatModel.dmChatUser1.username
                    val user2 = chatModel.dmChatUser2.username
                    deleteFromUserCollection(firestore, user1, chatModel.chatId) { isDeleted1 ->
                        deleteFromUserCollection(firestore, user2, chatModel.chatId) { isDeleted2 ->
                            onSuccess(isDeleted1 && isDeleted2)
                        }
                    }
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }

        private fun deleteFromUserCollection(
            firestore: FirebaseFirestore,
            username: String,
            chatId: String,
            onSuccess: (Boolean) -> Unit
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(username)
                .get().addOnSuccessListener { document ->
                    val userModel = document.toObject(UserModel::class.java)
                    if (userModel == null) {
                        onSuccess(false)
                        return@addOnSuccessListener
                    }
                    val newUserModel = userModel.copy(
                        favourites = userModel.favourites.filter {
                            it != chatId
                        }
                    )
                    firestore.collection(FirestoreCollections.USERS_COLLECTION)
                        .document(username)
                        .set(newUserModel, SetOptions.merge())
                        .addOnSuccessListener {
                            onSuccess(true)
                        }.addOnFailureListener {
                            onSuccess(false)
                        }
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}