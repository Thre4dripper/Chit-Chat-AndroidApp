package com.example.chitchatapp.firebase.firestore

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreUtils {
    companion object {
        fun checkInitialRegisteredUser(user: FirebaseUser?): Boolean {
            if (user == null) return false

            val firestore = Firebase.firestore
            val userRef = firestore.collection("Users").document(user.uid)

            return userRef.get().isSuccessful
        }
    }
}