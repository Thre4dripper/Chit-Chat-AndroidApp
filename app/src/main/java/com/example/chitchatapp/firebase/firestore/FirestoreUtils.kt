package com.example.chitchatapp.firebase.firestore

import android.content.Context
import com.example.chitchatapp.Constants
import com.example.chitchatapp.store.UserDetails
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

        fun getUserProfileImage(
            context: Context,
            user: FirebaseUser,
            profileImage: (String) -> Unit
        ) {
            getProfileFromUidDoc(user) { profileImageUrl1, _, _, _ ->
                if (profileImageUrl1.isNotEmpty()) {
                    profileImage(profileImageUrl1)
                } else {
                    getProfileFromUsernameDoc(context) { profileImageUrl2, _, _, _ ->
                        if (profileImageUrl2.isNotEmpty()) {
                            profileImage(profileImageUrl2)
                        } else {
                            profileImage("")
                        }
                    }
                }
            }
        }

        private fun getProfileFromUidDoc(
            user: FirebaseUser,
            profile: (String, String, String, String) -> Unit
        ) {
            val firestore = Firebase.firestore
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .document(user.uid)
                .get().addOnSuccessListener { uidDoc ->
                    if (uidDoc.exists()) {
                        val profileImageUrl = uidDoc["image"] as String
                        val username = uidDoc["username"] as String
                        val name = uidDoc["name"] as String
                        val bio = uidDoc["bio"] as String
                        profile(profileImageUrl, username, name, bio)
                    } else {
                        profile("", "", "", "")
                    }
                }.addOnFailureListener {
                    profile("", "", "", "")
                }
        }

        private fun getProfileFromUsernameDoc(
            context: Context,
            profile: (String, String, String, String) -> Unit
        ) {
            val usernameDoc = UserDetails.getUsername(context) ?: ""
            val firestore = Firebase.firestore
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .document(usernameDoc)
                .get().addOnSuccessListener { uidDoc ->
                    if (uidDoc.exists()) {
                        val profileImageUrl = uidDoc["image"] as String
                        val username = uidDoc["username"] as String
                        val name = uidDoc["name"] as String
                        val bio = uidDoc["bio"] as String
                        profile(profileImageUrl, username, name, bio)
                    } else {
                        profile("", "", "", "")
                    }
                }.addOnFailureListener {
                    profile("", "", "", "")
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