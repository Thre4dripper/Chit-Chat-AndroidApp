package com.example.chitchatapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.HomeChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeRepository {
    companion object {
        private val TAG = "HomeRepository"

        val homeChats = MutableLiveData<List<HomeChatModel>?>(null)

        fun getUsername(
            onSuccess: (String?) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            if(user == null) {
                onSuccess(null)
                return
            }

            CrudUtils.getFirestoreDocument(
                firestore,
                FirestoreCollections.REGISTERED_IDS_COLLECTION,
                user.uid,
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

        fun checkCompleteRegistration(
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            Utils.checkCompleteRegistration(firestore, user, onSuccess)
        }
    }
}