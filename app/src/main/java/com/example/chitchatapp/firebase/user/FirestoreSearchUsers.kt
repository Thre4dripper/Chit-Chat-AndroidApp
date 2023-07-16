package com.example.chitchatapp.firebase.user

import com.example.chitchatapp.Constants
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreSearchUsers {
    companion object {
        fun searchUsers(
            firestore: FirebaseFirestore,
            searchQuery: String,
            searchResult: (List<UserModel>) -> Unit
        ) {
            firestore.collection(Constants.FIRESTORE_USER_COLLECTION)
                .orderBy(Constants.FIRESTORE_USER_USERNAME)
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .get()
                .addOnSuccessListener {
                    val users = mutableListOf<UserModel>()
                    for (document in it) {
                        val user = document.toObject(UserModel::class.java)
                        users.add(user)
                    }
                    searchResult(users)
                }
                .addOnFailureListener {
                    println("Error getting documents: $it")
                }

        }
    }
}