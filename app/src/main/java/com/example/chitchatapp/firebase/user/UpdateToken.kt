package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.google.firebase.firestore.FirebaseFirestore

class UpdateToken {
    companion object {
        fun updateFCMToken(
            firestore: FirebaseFirestore,
            loggedInUser: String?,
            token: String,
        ) {
            if (loggedInUser == null) {
                return
            }
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(loggedInUser)
                .update(UserConstants.FCM_TOKEN, token)
        }
    }
}