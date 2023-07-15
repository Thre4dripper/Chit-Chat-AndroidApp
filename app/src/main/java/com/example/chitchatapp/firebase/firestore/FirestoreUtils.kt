package com.example.chitchatapp.firebase.firestore

import com.example.chitchatapp.Constants
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreUtils {
    companion object {
        private const val TAG = "FirestoreUtils"
        fun checkInitialRegisteredUser(user: FirebaseUser, isInitial: (Boolean) -> Unit) {

            val firestore = Firebase.firestore
            val userRef =
                firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user.uid)

            userRef.get().addOnSuccessListener {
                isInitial(it.exists())
            }.addOnFailureListener {
                isInitial(false)
            }
        }


        fun checkAvailableUsername(username: String, available: (Boolean) -> Unit) {
            val firestore = Firebase.firestore
            val userRef =
                firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)

            userRef.get().addOnSuccessListener {
                available(!it.exists())
            }.addOnFailureListener {
                available(false)
            }
        }

        fun updateUsername(username: String, user: FirebaseUser, success: (Boolean) -> Unit) {
            val firestore = Firebase.firestore
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(user.uid)
                .get().addOnSuccessListener {
                    val userMap = it.data
                    firestore.collection(Constants.FIRESTORE_USER_COLLECTION).document(username)
                        .set(userMap!!).addOnSuccessListener {
                            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                                .document(user.uid)
                                .delete().addOnSuccessListener {
                                    success(true)
                                }.addOnFailureListener {
                                    success(false)
                                }
                        }.addOnFailureListener {
                            success(false)
                        }
                }.addOnFailureListener {
                    success(false)
                }
        }
    }
}