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
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupId)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(GroupChatModel::class.java)

                    getUserTokens(firestore, groupId) { tokens ->
                        groupDetails(data, tokens)
                    }
                }
                .addOnFailureListener {
                    groupDetails(null, emptyList())
                }
        }

        private fun getUserTokens(
            firestore: FirebaseFirestore,
            groupId: String,
            tokens: (List<String>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .whereArrayContains(UserConstants.GROUPS, groupId)
                .get()
                .addOnSuccessListener {
                    val fcmTokens = mutableListOf<String>()

                    for (document in it) {
                        val userModel = document.toObject(UserModel::class.java)
                        fcmTokens.add(userModel.fcmToken)
                    }

                    tokens(fcmTokens)
                }
        }
    }
}