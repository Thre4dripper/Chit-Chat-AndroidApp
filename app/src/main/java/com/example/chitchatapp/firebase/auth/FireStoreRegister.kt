package com.example.chitchatapp.firebase.auth

import com.example.chitchatapp.Constants
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireStoreRegister {
    companion object {
        fun registerInitialUser(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            onSuccess: (Boolean) -> Unit
        ) {

            checkUserCompleteRegistration(user) {
                if (!it) {
                    val data = UserModel(
                        user.uid,
                        "",
                        user.displayName!!,
                        user.photoUrl.toString(),
                        "",
                        ""
                    )
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                        .document(user.uid)
                        .set(data)
                        .addOnSuccessListener {
                            onSuccess(true)
                        }.addOnFailureListener {
                            onSuccess(false)
                        }
                }
            }
        }

        private fun checkUserCompleteRegistration(
            user: FirebaseUser,
            onSuccess: (Boolean) -> Unit
        ) {
            val firestore = Firebase.firestore

            //add document to Users collection
            firestore.collection(Constants.FIRESTORE_REGISTERED_UID_COLLECTION)
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    onSuccess(it.exists())
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}