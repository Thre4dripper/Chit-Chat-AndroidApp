package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.ErrorMessages
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.SuccessMessages
import com.google.firebase.firestore.FirebaseFirestore

class UpdateGroup {
    companion object {
        fun updateGroupImage(
            fireStore: FirebaseFirestore,
            groupId: String,
            imageUrl: String?,
            callback: (String) -> Unit
        ) {
            fireStore.collection(FirestoreCollections.GROUPS_COLLECTION).document(groupId)
                .update(GroupConstants.GROUP_IMAGE, imageUrl)
                .addOnSuccessListener {
                    callback(SuccessMessages.GROUP_IMAGE_UPDATED_SUCCESSFULLY)
                }
                .addOnFailureListener {
                    callback(ErrorMessages.ERROR_UPDATING_GROUP_IMAGE)
                }
        }
    }
}