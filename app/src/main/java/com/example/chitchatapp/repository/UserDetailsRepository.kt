package com.example.chitchatapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.firebase.profile.GetProfile
import com.example.chitchatapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class UserDetailsRepository {
    companion object {
        val userDetails = MutableLiveData<UserModel>()

        fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) {
            val user = FirebaseAuth.getInstance().currentUser
            GetProfile.getProfile(context, user) { profile ->
                if (profile != null) {
                    userDetails.postValue(profile)
                    onSuccess(true)
                } else {
                    userDetails.postValue(null)
                    onSuccess(false)
                }
            }
        }
    }
}