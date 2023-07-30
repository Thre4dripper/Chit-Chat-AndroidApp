package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class GetGroupDetails {
    companion object {
        fun getDetails(
            firestore: FirebaseFirestore,
            groupId: String,
            groupDetails: (GroupChatModel?, List<String>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION).document(groupId).get()
                .addOnSuccessListener {
                    val groupModel = it.toObject(GroupChatModel::class.java)

                    if (groupModel == null) {
                        groupDetails(null, emptyList())
                        return@addOnSuccessListener
                    }

                    getUserTokens(firestore, groupModel) { tokens ->
                        groupDetails(groupModel, tokens)
                    }
                }.addOnFailureListener {
                    groupDetails(null, emptyList())
                }
        }

        private fun getUserTokens(
            firestore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            tokens: (List<String>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .whereArrayContains(UserConstants.GROUPS, groupChatModel.id).get()
                .addOnSuccessListener {
                    val fcmTokens = mutableListOf<String>()

                    for (document in it) {
                        val userModel = document.toObject(UserModel::class.java)

                        //do not take muted users tokens
                        if (groupChatModel.mutedBy.contains(userModel.username)) continue

                        fcmTokens.add(userModel.fcmToken)
                    }

                    tokens(fcmTokens)
                }
        }
    }
}