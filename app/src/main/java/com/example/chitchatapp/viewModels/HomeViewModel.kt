package com.example.chitchatapp.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.chitchatapp.enums.UserStatus
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

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun signInUser(): Intent {
        isFirebaseUILaunched = true
        return AuthRepository.signInUser()
    }

    fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
        isFirebaseUILaunched = false
        AuthRepository.signOutUser(context) {
            //clear all data on sign out
            UserRepository.updateStatus(context, UserStatus.LastSeen)
            UserRepository.updateToken(context, "")
            AddChatsRepository.searchResult.value = ArrayList()
            UserRepository.userDetails.value = null
            ChatsRepository.homeChats.value = null
            UserStore.saveUsername(context, null)
            onSuccess(it)
        }
    }

    fun onSignInResult(
        res: FirebaseAuthUIAuthenticationResult,
        onSuccess: (Boolean) -> Unit,
    ) {
        //init user details on sign in
        AuthRepository.onSignInResult(res, onSuccess)
    }

    /**
     * This function checks if the user is initially registered and completely registered
     * This is called when the app is launched from onCreate() of HomeActivity
     */
    fun checkUserRegistration(
        onSuccess: (Boolean) -> Unit,
    ) {
        HomeRepository.checkInitialRegistration(onSuccess)

        HomeRepository.checkCompleteRegistration {
            if (it) initUserDetails()
        }
    }

    private fun initUserDetails() {
        getCurrentUser() ?: return
        HomeRepository.getUsername { username ->
            if (username == null) return@getUsername

            val context = getApplication<Application>().applicationContext
            val fcmToken = UserStore.getFCMToken(context)

            //order of statements is important, first save username then do other stuff
            UserStore.saveUsername(context, username)
            UserRepository.getUserDetails(context) {}
            UserRepository.updateStatus(context, UserStatus.Online)
            UserRepository.updateToken(context, fcmToken)
        }
    }

    fun getHomeChats(
        context: Context
    ) {
        ChatsRepository.getAllUserChats(context)
        ChatsRepository.getAllGroupChats(context)
    }

    fun listenFavChats(
        username: String, onSuccess: (UserModel?) -> Unit
    ) {
        UserRepository.listenUserDetails(username, onSuccess)
    }
}