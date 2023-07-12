package com.example.chitchatapp.firebase.firestore

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreUtils {
    companion object {
        fun checkInitialRegisteredUser(user: FirebaseUser): Boolean {

            val firestore = Firebase.firestore
            val userRef = firestore.collection("Users").document(user.uid)

            return userRef.get().isSuccessful
        }

        fun getUserProfileImage(user: FirebaseUser, profileImage: (String) -> Unit) {
            val firestore = Firebase.firestore
            val userRef = firestore.collection("Users").document(user.uid)

            userRef.get().addOnSuccessListener {
                val profileImageUrl = it["image"] as String
                profileImage(profileImageUrl)
            }.addOnFailureListener {
                profileImage("")
            }
        }
    }
}