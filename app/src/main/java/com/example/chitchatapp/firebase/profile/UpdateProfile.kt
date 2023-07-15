package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.FirestoreUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfile {
    companion object {
        fun updateUsername(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
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

            getDataFromUsernameDocument(firestore, prevUsername!!) { userMap ->
                userMap?.set(Constants.FIRESTORE_USER_USERNAME, username)

                //update username
                createNewUsernameDocument(firestore, username, userMap!!) { isCreated ->
                    if (isCreated) {

                        //delete previous username document
                        deletePreviousUsernameDocument(firestore, prevUsername) { isDeleted ->
                            if (isDeleted) {

                                //update registered uid collection
                                updateUIDCollection(firestore, user, username) { isUpdated ->
                                    callback(
                                        if (isUpdated) Constants.USERNAME_UPDATED_SUCCESSFULLY
                                        else Constants.ERROR_UPDATING_USERNAME
                                    )
                                }
                            } else {
                                callback(Constants.ERROR_UPDATING_USERNAME)
                            }
                        }
                    }
                }
            }
        }

        private fun getDataFromUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            callback: (HashMap<String, Any>?) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username).get()
                .addOnSuccessListener {
                    val userMap = it.data
                    callback(userMap as HashMap<String, Any>?)
                }.addOnFailureListener {
                    callback(HashMap())
                }
        }

        private fun createNewUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            userMap: HashMap<String, Any>,
            callback: (Boolean) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                .set(userMap).addOnSuccessListener {
                    callback(true)
                }.addOnFailureListener {
                    callback(false)
                }
        }

        /**
         * PRIVATE FUNCTIONS POINTING TO FIRESTORE UTILS
         */
        private fun deletePreviousUsernameDocument(
            firestore: FirebaseFirestore, prevUsername: String, callback: (Boolean) -> Unit
        ) {
            FirestoreUtils.deleteFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                prevUsername
            ) { isDeleted ->
                callback(isDeleted)
            }
        }

        private fun updateUIDCollection(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            callback: (Boolean) -> Unit
        ) {
            FirestoreUtils.updateRegisteredUIDCollection(firestore, user, username) { isUpdated ->
                callback(isUpdated)
            }
        }
    }
}