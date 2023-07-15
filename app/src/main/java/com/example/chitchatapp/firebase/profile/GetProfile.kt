package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.Constants
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class GetProfile {
    companion object {
        fun getProfile(
            firestore: FirebaseFirestore,
            user: FirebaseUser?,
            username: String,
            profile: (UserModel?) -> Unit
        ) {
            getProfileFromUidDoc(firestore, user?.uid) { uidDocProfile ->
                if (uidDocProfile == null) {
                    getProfileFromUsernameDoc(firestore, username) { usernameDocProfile ->
                        profile(usernameDocProfile)
                    }
                } else {
                    profile(uidDocProfile)
                }
            }
        }

        private fun getProfileFromUidDoc(
            firestore: FirebaseFirestore,
            uid: String?,
            profile: (UserModel?) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .document(uid!!)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(UserModel::class.java)
                    profile(data)
                }
                .addOnFailureListener {
                    profile(null)
                }
        }

        private fun getProfileFromUsernameDoc(
            firestore: FirebaseFirestore,
            username: String,
            profile: (UserModel?) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .document(username)
                .get()
                .addOnSuccessListener {
                    val data = it.toObject(UserModel::class.java)
                    profile(data)
                }
                .addOnFailureListener {
                    profile(null)
                }
        }
    }
}