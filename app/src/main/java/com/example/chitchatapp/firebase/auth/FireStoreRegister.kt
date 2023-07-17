package com.example.chitchatapp.firebase.auth

import com.example.chitchatapp.constants.ErrorMessages
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.SuccessMessages
import com.example.chitchatapp.constants.UserConstants
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
                firestore.collection(FirestoreCollections.USERS_COLLECTION)
                    .document(user.uid)
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
                    callback(ErrorMessages.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }

                //get data from uid document
                getDataFromUIDDocument(firestore, user!!) { userMap ->
                    if (userMap == null) {
                        callback(ErrorMessages.ERROR_UPDATING_USERNAME)
                        return@getDataFromUIDDocument
                    }
                    userMap[UserConstants.USERNAME] = username

                    //create new document with username as document id
                    createNewUsernameDocument(firestore, username, userMap) { isCreated ->
                        if (!isCreated) {
                            callback(ErrorMessages.ERROR_UPDATING_USERNAME)
                            return@createNewUsernameDocument
                        }
                        //delete user document with uid as document id
                        deleteUIDDocument(firestore, user) { isDeleted ->
                            if (!isDeleted) {
                                callback(ErrorMessages.ERROR_UPDATING_USERNAME)
                                return@deleteUIDDocument
                            }

                            //register user with username as document id
                            registerInUIDCollection(firestore, user, username)
                            { isRegistered ->
                                callback(
                                    if (isRegistered) SuccessMessages.USERNAME_UPDATED_SUCCESSFULLY
                                    else ErrorMessages.ERROR_UPDATING_USERNAME
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
                FirestoreCollections.USERS_COLLECTION,
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
                FirestoreCollections.USERS_COLLECTION,
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
                FirestoreCollections.USERS_COLLECTION,
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
            data[UserConstants.USERNAME] = username
            CrudUtils.updateFirestoreDocument(
                firestore,
                FirestoreCollections.REGISTERED_IDS_COLLECTION,
                user.uid,
                data
            ) { isUpdated ->
                callback(isUpdated)
            }
        }

    }
}