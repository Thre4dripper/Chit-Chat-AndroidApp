package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfile {
    companion object {
        /**
         * Function to update username
         */
        fun updateUsername(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            prevUsername: String?,
            username: String,
            callback: (String) -> Unit
        ) {
            Utils.checkAvailableUsername(firestore, username) { isAvailable ->
                if (!isAvailable) {
                    callback(Constants.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }

                getDataFromUsernameDocument(firestore, prevUsername!!) { userMap ->
                    if (userMap == null) {
                        callback(Constants.ERROR_UPDATING_USERNAME)
                        return@getDataFromUsernameDocument
                    }
                    userMap[Constants.FIRESTORE_USER_USERNAME] = username

                    //create new document with username as document id
                    createNewUsernameDocument(firestore, username, userMap) { isCreated ->
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
        }


        /**
         * PRIVATE FUNCTIONS POINTING TO CRUD UTILS
         */
        private fun getDataFromUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            callback: (HashMap<String, Any>?) -> Unit
        ) {
            CrudUtils.getFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                username
            ) { userMap ->
                callback(userMap)
            }
        }

        private fun createNewUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            userMap: HashMap<String, Any>,
            callback: (Boolean) -> Unit
        ) {
            CrudUtils.createFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                username,
                userMap
            ) { isCreated ->
                callback(isCreated)
            }
        }

        private fun deletePreviousUsernameDocument(
            firestore: FirebaseFirestore, prevUsername: String, callback: (Boolean) -> Unit
        ) {
            CrudUtils.deleteFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                prevUsername
            ) { isDeleted ->
                callback(isDeleted)
            }
        }

        private fun updateUIDCollection(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            username: String,
            callback: (Boolean) -> Unit
        ) {
            val data = hashMapOf<String, Any>()
            data[Constants.FIRESTORE_USER_USERNAME] = username
            CrudUtils.updateFirestoreDocument(
                firestore,
                Constants.FIRESTORE_REGISTERED_UID_COLLECTION,
                user.uid,
                data
            ) { isUpdated ->
                callback(isUpdated)
            }
        }

        /**
         * Function to update name
         */
        fun updateName(
            firestore: FirebaseFirestore,
            username: String,
            name: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                .update(Constants.FIRESTORE_USER_NAME, name).addOnSuccessListener {
                    callback(Constants.NAME_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_NAME)
                }
        }

        /**
         * Function to update bio
         */
        fun updateBio(
            firestore: FirebaseFirestore,
            username: String,
            bio: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                .update(Constants.FIRESTORE_USER_BIO, bio).addOnSuccessListener {
                    callback(Constants.BIO_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_BIO)
                }
        }

        /**
         * Function to update profile picture
         */
        fun updateProfilePicture(
            firestore: FirebaseFirestore,
            username: String,
            profilePicture: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                .update(Constants.FIRESTORE_USER_PHOTO_URL, profilePicture)
                .addOnSuccessListener {
                    callback(Constants.PROFILE_PICTURE_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(Constants.ERROR_UPDATING_PROFILE_PICTURE)
                }
        }
    }
}