package com.example.chitchatapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.user.FirestoreSearchUsers
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class AddChatsRepository {
    companion object {
        val searchResult = MutableLiveData<ArrayList<UserModel>>()

        fun searchUsers(
            searchQuery: String,
        ) {
            val firestore = FirebaseFirestore.getInstance()

            FirestoreSearchUsers.searchUsers(firestore, searchQuery) {
                searchResult.value = it as ArrayList<UserModel>
            }
        }
    }
}