package com.example.chitchatapp.firebase.auth

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.FirestoreUtils
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreRegister {
    companion object {
        fun registerInitialUser(
            firestore: FirebaseFirestore, user: FirebaseUser, onSuccess: (Boolean) -> Unit
        ) {
            //check if user is already registered
            FirestoreUtils.checkCompleteRegistration(firestore, user) {
                if (it) {
                    return@checkCompleteRegistration
                }
            }

            val data = UserModel(
                user.uid, "", user.displayName!!, user.photoUrl.toString(), "", ""
            )

            //register user with uid as document id
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user.uid)
                .set(data).addOnSuccessListener {
                    onSuccess(true)
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun registerCompleteUser(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            callback: (String) -> Unit
        ) {
            FirestoreUtils.checkAvailableUsername(firestore, username) { isAvailable ->
                if (!isAvailable) {
                    callback(Constants.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }
            }

            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user!!.uid).get()
                .addOnSuccessListener {
                    val userMap = it.data
                    userMap?.set("username", username)
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                        .set(userMap!!).addOnSuccessListener {
                            deleteUidDoc(firestore, user, username, callback)
                        }.addOnFailureListener {
                            callback(Constants.ERROR_UPDATING_USERNAME)
                        }
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_USERNAME)
                }
        }

        private fun deleteUidDoc(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user!!.uid).delete()
                .addOnSuccessListener {
                    markRegisteredUsername(firestore, user, username, callback)
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_USERNAME)
                }
        }

        private fun markRegisteredUsername(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_REGISTERED_UID_COLLECTION).document(user!!.uid)
                .set(hashMapOf("username" to username)).addOnSuccessListener {
                    callback(Constants.USERNAME_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_USERNAME)
                }
        }
    }
}