package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class GetProfile {
    companion object {
        private const val TAG = "GetProfile"
        fun getProfile(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            username: String,
            profile: (UserModel?) -> Unit
        ) {
            getProfileFromUsernameDoc(firestore, username) { usernameDocProfile ->
                if (usernameDocProfile == null) {
                    getProfileFromUidDoc(firestore, user.uid) { uidDocProfile ->
                        profile(uidDocProfile)
                    }
                } else {
                    profile(usernameDocProfile)
                }
            }
        }

        private fun getProfileFromUidDoc(
            firestore: FirebaseFirestore,
            uid: String,
            profile: (UserModel?) -> Unit
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
                .document(uid)
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
            firestore.collection(FirestoreCollections.USERS_COLLECTION)
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