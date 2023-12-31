package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.models.GroupMessageModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CreateGroup {
    companion object {
        fun createNewGroup(
            firestore: FirebaseFirestore,
            groupChatId: String,
            groupName: String,
            groupImageUrl: String?,
            loggedInUsername: String,
            selectedUsers: List<GroupChatUserModel>,
            onSuccess: (String?) -> Unit,
        ) {
            val group = GroupChatModel(
                groupChatId,
                groupName,
                groupImageUrl,
                selectedUsers,
                listOf(
                    GroupMessageModel(
                        UUID.randomUUID().toString(),
                        GroupMessageType.TypeCreatedGroup,
                        null,
                        null,
                        null,
                        Timestamp.now(),
                        listOf(loggedInUsername),
                        loggedInUsername,
                    )
                ),
                listOf()
            )


            //add in groups collection
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION).document(groupChatId)
                .set(group)
                .addOnSuccessListener {
                    onSuccess(groupChatId)
                }
                .addOnFailureListener {
                    onSuccess(null)
                }

            //add in users collection
            selectedUsers.forEach { user ->
                GetDetails.getUserDetails(firestore, user.username) {
                    if (it == null) return@getUserDetails
                    val updatedUserModel = it.copy(
                        groups = it.groups.toMutableList().apply {
                            add(groupChatId)
                        }
                    )

                    firestore.collection(FirestoreCollections.USERS_COLLECTION)
                        .document(user.username)
                        .set(updatedUserModel)
                }
            }
        }
    }
}