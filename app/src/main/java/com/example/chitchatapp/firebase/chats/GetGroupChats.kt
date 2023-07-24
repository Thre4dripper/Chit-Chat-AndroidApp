package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.ChatGroupModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class GetGroupChats {
    companion object {
        fun getAllGroupChats(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (List<ChatGroupModel>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .where(
                    Filter.arrayContains("members", username)
                )
                .addSnapshotListener() { value, error ->
                    if (error != null) {
                        onSuccess(listOf())
                        return@addSnapshotListener
                    }

                    val groupChats = mutableListOf<ChatGroupModel>()
                    for (doc in value!!) {
                        val groupChat = doc.toObject(ChatGroupModel::class.java)
                        groupChats.add(groupChat)
                    }

                    onSuccess(groupChats)
                }
        }
    }
}