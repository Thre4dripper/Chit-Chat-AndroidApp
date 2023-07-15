package com.example.chitchatapp.firebase.profile

import android.content.Context
import com.example.chitchatapp.Constants
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.store.UserDetails
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class GetProfile {
    companion object {
        fun getProfile(context: Context, user: FirebaseUser?, profile: (UserModel?) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()

            getProfileFromUidDoc(firestore, user?.uid) { uidDocProfile ->
                if (uidDocProfile == null) {
                    val username = UserDetails.getUsername(context) ?: ""
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