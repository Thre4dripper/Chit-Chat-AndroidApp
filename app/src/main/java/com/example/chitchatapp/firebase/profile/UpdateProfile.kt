package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.FirestoreUtils
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfile {
    companion object {
        fun updateUsername(
            firestore: FirebaseFirestore,
            prevUsername: String?,
            username: String,
            callback: (String) -> Unit
        ) {
            FirestoreUtils.checkAvailableUsername(firestore, username) { isAvailable ->
                if (!isAvailable) {
                    callback(Constants.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }
            }

            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .document(prevUsername!!)
                .get().addOnSuccessListener {
                    val userMap = it.data
                    userMap?.set("username", username)
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                        .set(userMap!!).addOnSuccessListener {
                            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                                .document(prevUsername)
                                .delete().addOnSuccessListener {
                                    callback(Constants.USERNAME_UPDATED_SUCCESSFULLY)
                                }.addOnFailureListener {
                                    callback(Constants.ERROR_UPDATING_USERNAME)
                                }
                        }.addOnFailureListener {
                            callback(Constants.ERROR_UPDATING_USERNAME)
                        }
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_USERNAME)
                }
        }
    }
}