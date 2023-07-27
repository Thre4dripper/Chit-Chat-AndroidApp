package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class CommonGroups {
    companion object {
        fun findCommonGroups(
            firestore: FirebaseFirestore,
            chatUserModel: GroupChatUserModel,
            loggedInUserModel: GroupChatUserModel,
            onSuccess: (List<GroupChatModel>) -> Unit
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .where(
                    Filter.and(
                        Filter.arrayContains(
                            GroupConstants.GROUP_MEMBERS,
                            chatUserModel
                        ),
                        Filter.arrayContains(
                            GroupConstants.GROUP_MEMBERS,
                            loggedInUserModel
                        )
                    )
                )
                .addSnapshotListener() { value, error ->
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