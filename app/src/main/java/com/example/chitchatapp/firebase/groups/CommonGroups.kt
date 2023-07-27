package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.google.firebase.firestore.FirebaseFirestore

class CommonGroups {
    companion object {
        fun findCommonGroups(
            firestore: FirebaseFirestore,
            chatUserModel: GroupChatUserModel,
            loggedInUsername: String,
            onSuccess: (List<GroupChatModel>) -> Unit
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .whereArrayContains(
                    GroupConstants.GROUP_MEMBERS,
                    chatUserModel
                )
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    val commonGroups = mutableListOf<GroupChatModel>()

                    value?.documents?.forEach { document ->
                        val groupChatModel = document.toObject(GroupChatModel::class.java)
                        if (groupChatModel?.members?.find { member ->
                                member.username == loggedInUsername
                            } != null)
                            commonGroups.add(groupChatModel)

                    }
                    onSuccess(commonGroups)
                }
        }
    }
}