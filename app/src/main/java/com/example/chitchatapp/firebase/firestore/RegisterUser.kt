package com.example.chitchatapp.firebase.firestore

import com.example.chitchatapp.Constants
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterUser {
    companion object {
        fun registerInitialUser(user: FirebaseUser, onSuccess: (Boolean) -> Unit) {
            val firestore = Firebase.firestore

            checkUserCompleteRegistration(user) {
                if (!it) {
                    val data = hashMapOf(
                        "username" to "",
                        "name" to user.displayName,
                        "bio" to "",
                        "profileImage" to user.photoUrl.toString(),
                    )
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                        .document(user.uid)
                        .set(data)
                        .addOnSuccessListener {
                            onSuccess(true)
                        }.addOnFailureListener {
                            onSuccess(false)
                        }
                }
            }
        }

        private fun checkUserCompleteRegistration(
            user: FirebaseUser,
            onSuccess: (Boolean) -> Unit
        ) {
            val firestore = Firebase.firestore

            //add document to Users collection
            firestore.collection(Constants.FIRESTORE_REGISTERED_UID_COLLECTION)
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    onSuccess(it.exists())
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}