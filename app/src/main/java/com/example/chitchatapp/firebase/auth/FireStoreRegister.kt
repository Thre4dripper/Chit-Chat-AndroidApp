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
                .addOnSuccessListener { uidDoc ->
                    val userMap = uidDoc.data
                    userMap?.set(Constants.FIRESTORE_USER_USERNAME, username)
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                        .set(userMap!!).addOnSuccessListener {

                            //delete user document with uid as document id
                            deleteUIDDocument(firestore, user) { isDeleted ->
                                if (isDeleted) {

                                    //register user with username as document id
                                    registerInUIDCollection(firestore, user, username)
                                    { isRegistered ->
                                        callback(
                                            if (isRegistered) Constants.USERNAME_UPDATED_SUCCESSFULLY
                                            else Constants.ERROR_UPDATING_USERNAME
                                        )
                                    }
                                } else {
                                    callback(Constants.ERROR_UPDATING_USERNAME)
                                }
                            }

                        }.addOnFailureListener {
                            callback(Constants.ERROR_UPDATING_USERNAME)
                        }
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_USERNAME)
                }
        }

        /**
         * PRIVATE FUNCTIONS POINTING TO FIRESTORE UTILS
         */
        private fun deleteUIDDocument(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            callback: (Boolean) -> Unit
        ) {
            FirestoreUtils.deleteFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                user.uid
            ) { isDeleted ->
                callback(isDeleted)
            }
        }

        private fun registerInUIDCollection(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            username: String,
            callback: (Boolean) -> Unit
        ) {
            FirestoreUtils.updateRegisteredUIDCollection(firestore, user, username, callback)
        }

    }
}