package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RemoveGroup {
    companion object {
        fun removeFromUserCollection(
            fireStore: FirebaseFirestore,
            loggedInUsername: String,
            groupId: String,
            onSuccess: (Boolean) -> Unit
        ) {
            GetDetails.getUserDetails(fireStore, loggedInUsername) {
                if (it == null) {
                    onSuccess(false)
                    return@getUserDetails
                }
                //remove group from user collection
                val updatedUserModel = it.copy(
                    groups = it.groups.filter { id ->
                        id != groupId
                    }
                )
                fireStore.collection(FirestoreCollections.USERS_COLLECTION)
                    .document(loggedInUsername)
                    .set(updatedUserModel, SetOptions.merge())
                    .addOnSuccessListener {
                        onSuccess(true)
                    }
                    .addOnFailureListener {
                        onSuccess(false)
                    }
            }
        }
    }
}