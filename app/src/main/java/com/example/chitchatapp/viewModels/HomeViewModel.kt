package com.example.chitchatapp.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.models.HomeChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.AddChatsRepository
import com.example.chitchatapp.repository.AuthRepository
import com.example.chitchatapp.repository.GroupChatsRepository
import com.example.chitchatapp.repository.HomeRepository
import com.example.chitchatapp.repository.UserChatsRepository
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

    private val _homeChats = HomeRepository.homeChats
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
            HomeRepository.homeChats.value = null
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
            //init user details everytime even if the user is not completely registered
            //it will handle it inside the function

            // here the code differ from web implementation because in web there is no observer pattern like livedata
            initUserDetails()
        }
    }

    private fun initUserDetails() {
        val context = getApplication<Application>().applicationContext
        val fcmToken = UserStore.getFCMToken(context)
        Log.d("FCM", "initUserDetails: $fcmToken")

        HomeRepository.getUsername { username ->
            //save username even if it is null
            UserStore.saveUsername(context, username)

            //when username is null,then user details will be fetched from uid doc
            UserRepository.getUserDetails(context) {}

            //username is null when the user is not completely registered
            if (username == null) return@getUsername
            //update status and token only when username is not null
            UserRepository.updateStatus(context, UserStatus.Online)
            UserRepository.updateToken(context, fcmToken)
        }
    }

    fun getHomeChats(
        context: Context
    ) {
        UserChatsRepository.getAllUserChats(context)
        GroupChatsRepository.getAllGroupChats(context)
    }

    fun listenFavChats(
        username: String, onSuccess: (UserModel?) -> Unit
    ) {
        UserRepository.listenUserDetails(username, onSuccess)
    }
}