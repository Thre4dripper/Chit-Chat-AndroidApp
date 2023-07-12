package com.example.chitchatapp

class Constants {
    companion object {
        //firestore collection names
        const val FIRESTORE_USER_COLLECTION = "Users"
        const val FIRESTORE_REGISTERED_UID_COLLECTION = "RegisteredUIDs"

        //for passing set user details fragment
        const val FRAGMENT_TYPE = "fragment_type"
        const val FRAGMENT_USERNAME = "fragment_username"
        const val FRAGMENT_NAME = "fragment_name"
        const val FRAGMENT_BIO = "fragment_bio"
    }
}