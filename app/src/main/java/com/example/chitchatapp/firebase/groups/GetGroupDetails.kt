package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.GroupChatModel
import com.google.firebase.firestore.FirebaseFirestore

class GetGroupDetails {
    companion object {
        fun getDetails(
            firestore: FirebaseFirestore,
            groupId: String,
            groupDetails: (GroupChatModel?, tokens: List<String>) -> Unit,
        ) {
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupId)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(GroupChatModel::class.java)
                    val fcmTokens = mutableListOf<String>()

                    data?.members?.forEachIndexed { i, model ->
                        //get tokens of all members
                        GetDetails.getUserDetails(
                            firestore,
                            model.username
                        ) { userModel ->
                            fcmTokens.add(userModel?.fcmToken ?: "")

                            // If this is the last member, return the list of tokens and group details
                            if (i == data.members.size - 1) {
                                groupDetails(data, fcmTokens)
                            }
                        }
                    }

                }
                .addOnFailureListener {
                    groupDetails(null, emptyList())
                }
        }
    }
}