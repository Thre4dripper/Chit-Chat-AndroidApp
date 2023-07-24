package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.enums.UserStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class UpdateStatus {
    companion object {
        private val TAG = "UpdateStatus"

        fun updateUserStatus(
            firestore: FirebaseFirestore,
            loggedInUser: String?,
            status: UserStatus,
        ) {

            //todo update status in when login and logout
            if (loggedInUser == null) {
                return
            }
            val userStatus = if (status == UserStatus.Online) {
                status.name
            } else {
                "${status.name} ${Timestamp.now().seconds}"
            }
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(loggedInUser)
                .update("status", userStatus)
        }
    }
}