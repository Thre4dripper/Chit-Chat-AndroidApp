package com.example.chitchatapp.firebase.utils

import com.example.chitchatapp.Constants
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreUtils {
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
                .get().addOnSuccessListener {
                    isInitial(it.exists())
                }.addOnFailureListener {
                    isInitial(false)
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
    }
}