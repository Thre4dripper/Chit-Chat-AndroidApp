package com.example.chitchatapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.firebase.chats.GetChats
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.store.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeRepository {
    companion object {
        private val TAG = "HomeRepository"
        val homeChats = MutableLiveData<List<ChatModel>>(listOf())
        fun getUsername(
            onSuccess: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            CrudUtils.getFirestoreDocument(
                firestore,
                FirestoreCollections.REGISTERED_IDS_COLLECTION,
                user!!.uid,
                onSuccess = { data ->
                    onSuccess(data?.get(UserConstants.USERNAME) as String?)
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

        fun getChats(
            context: Context
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserDetails.getUsername(context) ?: ""
            GetChats.getAllUserChats(firestore, loggedInUser) {
                homeChats.value = it
            }
        }
    }
}