package com.example.chitchatapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.example.chitchatapp.firebase.profile.GetProfile
import com.example.chitchatapp.firebase.profile.UpdateProfile
import com.example.chitchatapp.firebase.utils.FirestoreUtils
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDetailsRepository {
    companion object {
        val userDetails = MutableLiveData<UserModel>()

        fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            GetProfile.getProfile(context, firestore, user) { profile ->
                if (profile != null) {
                    userDetails.postValue(profile)
                    onSuccess(true)
                } else {
                    userDetails.postValue(null)
                    onSuccess(false)
                }
            }
        }

        fun updateUsername(
            username: String,
            callback: (String) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            FirestoreUtils.checkCompleteRegistration(firestore, user) { isComplete ->
                if (!isComplete) {
                    FireStoreRegister.registerCompleteUser(firestore, user, username) { message ->
                        callback(message)
                    }
                    return@checkCompleteRegistration
                }

                val prevUsername = userDetails.value?.username
                UpdateProfile.updateUsername(firestore,user, prevUsername, username) { message ->
                    callback(message)
                }

                //updating in livedata
                userDetails.value = userDetails.value?.copy(username = username)
            }
        }
    }
}