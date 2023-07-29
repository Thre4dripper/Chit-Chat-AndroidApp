package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class GetDetails {
    companion object {
        fun getLiveUserDetails(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (UserModel?) -> Unit,
        ) {

            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(username)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        onSuccess(null)
                        return@addSnapshotListener
                    }

                    val data = value?.toObject(UserModel::class.java)
                    onSuccess(data)
                }
        }

        fun getUserDetails(
            firestore: FirebaseFirestore,
            username: String,
            onSuccess: (UserModel?) -> Unit,
        ) {

            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(username)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(UserModel::class.java)
                    onSuccess(data)
                }
                .addOnFailureListener {
                    onSuccess(null)
                }
        }
    }
}