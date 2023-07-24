package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore

class GetStatus {
    companion object {
        fun getUserStatus(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (String?) -> Unit,
        ) {

            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(username)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onSuccess(null)
                        return@addSnapshotListener
                    }

                    val status = value?.get("status") as String?
                    onSuccess(status)
                }
        }
    }
}