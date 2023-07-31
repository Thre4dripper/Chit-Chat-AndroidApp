package com.example.chitchatapp.firebase.chats

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.GroupConstants
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
                    Filter.arrayContains(GroupConstants.GROUP_MEMBERS, groupUser)
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

        fun getGroupChatById(
            firestore: FirebaseFirestore,
            groupId: String,
            onSuccess: (GroupChatModel?) -> Unit
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION).document(groupId)
                .get()
                .addOnSuccessListener {
                    if (it == null) {
                        onSuccess(null)
                        return@addOnSuccessListener
                    }

                    val groupChatModel = it.toObject(GroupChatModel::class.java)
                    onSuccess(groupChatModel)
                }
        }

        fun getLiveGroupChatById(
            firestore: FirebaseFirestore,
            groupId: String,
            onSuccess: (GroupChatModel?) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION).document(groupId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onSuccess(null)
                        return@addSnapshotListener
                    }

                    val groupChat = value?.toObject(GroupChatModel::class.java)
                    onSuccess(groupChat)
                }
        }
    }
}