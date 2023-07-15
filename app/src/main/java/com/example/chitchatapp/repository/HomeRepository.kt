package com.example.chitchatapp.repository

import com.example.chitchatapp.firebase.utils.FirestoreUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeRepository {
    companion object {
        fun getUsername(
            onSuccess: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            FirestoreUtils.getUsernameFromUIDCollection(firestore, user, onSuccess)
        }

        fun checkInitialRegistration(
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            FirestoreUtils.checkInitialRegistration(firestore, user, onSuccess)
        }
    }
}