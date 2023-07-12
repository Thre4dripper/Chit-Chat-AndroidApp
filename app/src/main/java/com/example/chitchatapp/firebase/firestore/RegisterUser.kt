package com.example.chitchatapp.firebase.firestore

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class RegisterUser {
    companion object {
        fun registerInitialUser(user: FirebaseUser?) {
            if (user == null) return
            val firestore = Firebase.firestore

            //add document to Users collection
            val userRef = firestore.collection("Users").document(user.uid)

            //create a hashmap to store user data
            val userHashMap = hashMapOf(
                "uid" to user.uid,
                "name" to user.displayName,
                "email" to user.email,
                "image" to user.photoUrl.toString(),
                "status" to "offline",
                "search" to user.displayName?.lowercase()
            )

            //add user data to document
            userRef.set(userHashMap)
        }
    }
}