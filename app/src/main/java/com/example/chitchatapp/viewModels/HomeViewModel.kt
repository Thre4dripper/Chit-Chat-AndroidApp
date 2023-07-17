package com.example.chitchatapp.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.AuthRepository
import com.example.chitchatapp.repository.HomeRepository
import com.example.chitchatapp.repository.UserDetailsRepository
import com.example.chitchatapp.store.UserDetails
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"

    val profileImage = Transformations.map(UserDetailsRepository.userDetails) {
        it?.profileImage
    }

    var isFirebaseUILaunched = false

    private val _homeChats = HomeRepository.homeChats
    val homeChats: LiveData<List<ChatModel>>
        get() = _homeChats

    init {
        initUserDetails()
    }

    /**
     * Init user details on app launch
     */
    private fun initUserDetails() {
        getCurrentUser() ?: return
        HomeRepository.getUsername { username ->
            if (username == null)
                return@getUsername

            UserDetails.saveUsername(getApplication<Application>().applicationContext, username)
            UserDetailsRepository.getUserDetails(getApplication<Application>().applicationContext) {}
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun signInUser(): Intent {
        isFirebaseUILaunched = true
        return AuthRepository.signInUser()
    }

    fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
        isFirebaseUILaunched = false

        //clear all data on sign out
        AddChatsRepository.searchResult.value = ArrayList()
        UserDetailsRepository.userDetails.value = null
        UserDetails.saveUsername(context, null)
        AuthRepository.signOutUser(context, onSuccess)
    }

    fun onSignInResult(
        res: FirebaseAuthUIAuthenticationResult,
        onSuccess: (Boolean) -> Unit,
    ) {
        //init user details on sign in
        initUserDetails()
        AuthRepository.onSignInResult(res, onSuccess)
    }

    fun checkInitialRegistration(
        onSuccess: (Boolean) -> Unit,
    ) =
        HomeRepository.checkInitialRegistration(onSuccess)

    fun getChats(
        context: Context
    ) = HomeRepository.getChats(context)
}