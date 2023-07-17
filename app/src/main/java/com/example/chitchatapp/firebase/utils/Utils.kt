package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.Constants
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Utils {
    companion object {
        private const val TAG = "FirestoreUtils"

        /**
         * Function to Check if the user is already initial registered as uid document in firestore
         */
        fun checkInitialRegistration(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            isInitial: (Boolean) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user!!.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        isInitial(false)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        isInitial(true)
                    } else {
                        isInitial(false)
                    }
                }
        }

        /**
         * Function to Check if the user is already complete registered as username document in firestore
         */
        fun checkCompleteRegistration(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            isComplete: (Boolean) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_REGISTERED_UID_COLLECTION).document(user!!.uid)
                .get().addOnSuccessListener {
                    isComplete(it.exists())
                }.addOnFailureListener {
                    isComplete(false)
                }
        }

        /**
         * Function to Check if the username is available in firestore
         */
        fun checkAvailableUsername(
            firestore: FirebaseFirestore,
            username: String,
            available: (Boolean) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                .get().addOnSuccessListener {
                    available(!it.exists())
                }.addOnFailureListener {
                    available(false)
                }
        }

        fun getChatDocId(
            chatUserId1: String,
            chatUserId2: String,
        ): String {
            return if (chatUserId1 < chatUserId2)
                "$chatUserId1-$chatUserId2"
            else
                "$chatUserId2-$chatUserId1"
        }
    }
}