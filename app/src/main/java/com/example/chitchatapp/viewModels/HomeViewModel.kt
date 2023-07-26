package com.example.chitchatapp.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.chitchatapp.models.HomeChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.AuthRepository
import com.example.chitchatapp.repository.ChatsRepository
import com.example.chitchatapp.repository.HomeRepository
import com.example.chitchatapp.repository.UserRepository
import com.example.chitchatapp.store.UserStore
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"

    private val _userDetails = UserRepository.userDetails
    val userDetails: LiveData<UserModel?>
        get() = _userDetails
    var isFirebaseUILaunched = false

    private val _homeChats = ChatsRepository.homeChats
    val homeChats: LiveData<List<HomeChatModel>?>
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

            UserStore.saveUsername(getApplication<Application>().applicationContext, username)
            UserRepository.getUserDetails(getApplication<Application>().applicationContext) {}
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
        UserRepository.userDetails.value = null
        ChatsRepository.homeChats.value = null
        UserStore.saveUsername(context, null)
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

    fun getHomeChats(
        context: Context
    ) {
        ChatsRepository.getAllUserChats(context)
        ChatsRepository.getAllGroupChats(context)
    }

    fun listenFavChats(
        username: String,
        onSuccess: (UserModel?) -> Unit
    ) {
        UserRepository.listenUserDetails(username, onSuccess)
    }
}