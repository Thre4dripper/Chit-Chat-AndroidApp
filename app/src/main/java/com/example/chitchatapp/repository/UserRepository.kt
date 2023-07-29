package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.ErrorMessages
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.example.chitchatapp.firebase.profile.GetProfile
import com.example.chitchatapp.firebase.profile.UpdateProfile
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.firebase.user.UpdateStatus
import com.example.chitchatapp.firebase.user.UpdateToken
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRepository {
    companion object {
        private const val TAG = "UserDetailsRepository"
        val userDetails = MutableLiveData<UserModel?>(null)

        fun getUserDetails(context: Context, onSuccess: (Boolean) -> Unit) {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val username = UserStore.getUsername(context) ?: ""

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

        fun listenUserDetails(
            username: String, onSuccess: (UserModel?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            GetDetails.getLiveUserDetails(firestore, username, onSuccess)
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
            val username = UserStore.getUsername(context) ?: ""

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
            val username = UserStore.getUsername(context) ?: ""

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
            val username = UserStore.getUsername(context) ?: ""
            val prevProfilePicture = userDetails.value!!.profileImage

            StorageUtils.getUrlFromStorage(
                storage, "${StorageFolders.PROFILE_IMAGES_FOLDER}/$username", profilePicture
            ) { url ->
                if (url != null) {
                    UpdateProfile.updateProfilePicture(
                        firestore, username, prevProfilePicture, url
                    ) {
                        //updating in livedata
                        userDetails.value = userDetails.value?.copy(profileImage = url)
                        callback(it)
                    }
                } else {
                    callback(ErrorMessages.ERROR_UPDATING_PROFILE_PICTURE)
                }
            }
        }

        fun updateStatus(
            context: Context,
            status: UserStatus,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val username = UserStore.getUsername(context)

            UpdateStatus.updateUserStatus(firestore, username, status)
        }

        fun updateToken(context: Context, fcmToken: String?) {
            val firestore = FirebaseFirestore.getInstance()
            val username = UserStore.getUsername(context)

            if (fcmToken == null || username == null) return

            UpdateToken.updateFCMToken(firestore, username, fcmToken)
        }
    }
}