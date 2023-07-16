package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.Constants
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.example.chitchatapp.firebase.profile.GetProfile
import com.example.chitchatapp.firebase.profile.UpdateProfile
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.store.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserDetailsRepository {
    companion object {
        private const val TAG = "UserDetailsRepository"
        val userDetails = MutableLiveData<UserModel>()

        fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val username = UserDetails.getUsername(context) ?: ""

            GetProfile.getProfile(firestore, user, username) { profile ->
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

            Utils.checkCompleteRegistration(firestore, user) { isComplete ->
                if (!isComplete) {
                    FireStoreRegister.registerCompleteUser(firestore, user, username) {
                        //updating in livedata
                        userDetails.value = userDetails.value?.copy(username = username)
                        callback(it)
                    }
                    return@checkCompleteRegistration
                }

                val prevUsername = userDetails.value?.username
                UpdateProfile.updateUsername(firestore, user!!, prevUsername, username) {
                    //updating in livedata
                    userDetails.value = userDetails.value?.copy(username = username)
                    callback(it)
                }

            }
        }

        fun updateName(
            context: Context,
            name: String,
            callback: (String) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val username = UserDetails.getUsername(context) ?: ""

            UpdateProfile.updateName(firestore, username, name) {
                //updating in livedata
                userDetails.value = userDetails.value?.copy(name = name)
                callback(it)
            }
        }

        fun updateBio(
            context: Context,
            bio: String,
            callback: (String) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val username = UserDetails.getUsername(context) ?: ""

            UpdateProfile.updateBio(firestore, username, bio) {
                //updating in livedata
                userDetails.value = userDetails.value?.copy(bio = bio)
                callback(it)
            }
        }

        fun updateProfilePicture(
            context: Context,
            profilePicture: Uri,
            callback: (String) -> Unit,
        ) {
            val storage = FirebaseStorage.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val username = UserDetails.getUsername(context) ?: ""

            StorageUtils.getUrlFromStorage(
                storage,
                "${Constants.FIREBASE_STORAGE_PROFILE_IMAGES}/$username",
                profilePicture
            ) { url ->
                if (url != null) {
                    UpdateProfile.updateProfilePicture(firestore, username, url) {
                        //updating in livedata
                        userDetails.value = userDetails.value?.copy(profileImage = url)
                        callback(it)
                    }
                } else {
                    callback(Constants.ERROR_UPDATING_PROFILE_PICTURE)
                }
            }
        }
    }
}