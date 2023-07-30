package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupMessageModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.UUID

class ExitGroup {
    companion object {
        fun exitGroup(
            fireStore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            loggedInUsername: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            val oldMembers = groupChatModel.members
            val newMembers = oldMembers.filter { member ->
                member.username != loggedInUsername
            }

            val oldMessagesList = groupChatModel.messages
            val newMessagesList = oldMessagesList.toMutableList()
            newMessagesList.add(
                0,
                GroupMessageModel(
                    UUID.randomUUID().toString(),
                    GroupMessageType.TypeLeavedMember,
                    null,
                    null,
                    null,
                    Timestamp.now(),
                    listOf(loggedInUsername),
                    loggedInUsername,
                )
            )

            val newGroupChatModel =
                groupChatModel.copy(members = newMembers, messages = newMessagesList)
            fireStore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .set(newGroupChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}