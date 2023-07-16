package com.example.chitchatapp.firebase.auth

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreRegister {
    companion object {
        private const val TAG = "FireStoreRegister"
        fun registerInitialUser(
            firestore: FirebaseFirestore, user: FirebaseUser, onSuccess: (Boolean) -> Unit
        ) {
            //check if user is already registered
            Utils.checkCompleteRegistration(firestore, user) {
                if (it) {
                    onSuccess(true)
                    return@checkCompleteRegistration
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
        }

        fun registerCompleteUser(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            callback: (String) -> Unit
        ) {
            Utils.checkAvailableUsername(firestore, username) { isAvailable ->
                if (!isAvailable) {
                    callback(Constants.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }

                //get data from uid document
                getDataFromUIDDocument(firestore, user!!) { userMap ->
                    if (userMap == null) {
                        callback(Constants.ERROR_UPDATING_USERNAME)
                        return@getDataFromUIDDocument
                    }
                    userMap[Constants.FIRESTORE_USER_USERNAME] = username

                    //create new document with username as document id
                    createNewUsernameDocument(firestore, username, userMap) { isCreated ->
                        if (!isCreated) {
                            callback(Constants.ERROR_UPDATING_USERNAME)
                            return@createNewUsernameDocument
                        }
                        //delete user document with uid as document id
                        deleteUIDDocument(firestore, user) { isDeleted ->
                            if (!isDeleted) {
                                callback(Constants.ERROR_UPDATING_USERNAME)
                                return@deleteUIDDocument
                            }

                            //register user with username as document id
                            registerInUIDCollection(firestore, user, username)
                            { isRegistered ->
                                callback(
                                    if (isRegistered) Constants.USERNAME_UPDATED_SUCCESSFULLY
                                    else Constants.ERROR_UPDATING_USERNAME
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * PRIVATE FUNCTIONS POINTING TO FIRESTORE UTILS
         */
        private fun getDataFromUIDDocument(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            callback: (HashMap<String, Any>?) -> Unit
        ) {
            CrudUtils.getFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                user.uid
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

        private fun deleteUIDDocument(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            callback: (Boolean) -> Unit
        ) {
            CrudUtils.deleteFirestoreDocument(
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

    }
}