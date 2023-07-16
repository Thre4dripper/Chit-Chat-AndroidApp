package com.example.chitchatapp

class Constants {
    companion object {
        //firestore collection names
        const val FIRESTORE_USER_COLLECTION = "Users"
        const val FIRESTORE_REGISTERED_UID_COLLECTION = "RegisteredUIDs"

        //firestore document fields
//        const val FIRESTORE_USER_ID = "uid"
        const val FIRESTORE_USER_NAME = "name"
        const val FIRESTORE_USER_USERNAME = "username"
//        const val FIRESTORE_USER_PHOTO_URL = "profileImage"
        const val FIRESTORE_USER_BIO = "bio"

        //update user details constants
        const val USERNAME_ALREADY_EXISTS = "Username already exists"
        const val USERNAME_UPDATED_SUCCESSFULLY = "Username updated"
        const val ERROR_UPDATING_USERNAME = "Error updating username"
        const val NAME_UPDATED_SUCCESSFULLY = "Name updated"
        const val ERROR_UPDATING_NAME = "Error updating name"
        const val BIO_UPDATED_SUCCESSFULLY = "Bio updated"
        const val ERROR_UPDATING_BIO = "Error updating bio"

        //for passing set user details fragment
        const val FRAGMENT_TYPE = "fragment_type"
        const val FRAGMENT_USERNAME = "fragment_username"
        const val FRAGMENT_NAME = "fragment_name"
        const val FRAGMENT_BIO = "fragment_bio"
    }
}