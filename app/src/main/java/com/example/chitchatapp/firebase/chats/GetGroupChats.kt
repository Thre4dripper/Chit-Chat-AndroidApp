package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class GetGroupChats {
    companion object {
        fun getAllGroupChats(
            firestore: FirebaseFirestore,
            groupUser: GroupChatUserModel,
            onSuccess: (List<GroupChatModel>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .where(
                    Filter.arrayContains(ChatConstants.GROUP_MEMBERS, groupUser)
                )
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onSuccess(listOf())
                        return@addSnapshotListener
                    }

                    val groupChats = mutableListOf<GroupChatModel>()
                    for (doc in value!!) {
                        val groupChat = doc.toObject(GroupChatModel::class.java)
                        groupChats.add(groupChat)
                    }

                    onSuccess(groupChats)
                }
        }
    }
}