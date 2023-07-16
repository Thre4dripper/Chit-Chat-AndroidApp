package com.example.chitchatapp.repository

import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeRepository {
    companion object {
        fun getUsername(
            onSuccess: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            CrudUtils.getFirestoreDocument(
                firestore,
                Constants.FIRESTORE_USER_COLLECTION,
                user!!.uid,
                onSuccess = { data ->
                    onSuccess(data?.get(Constants.FIRESTORE_USER_USERNAME) as String?)
                }
            )
        }

        fun checkInitialRegistration(
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            Utils.checkInitialRegistration(firestore, user, onSuccess)
        }
    }
}