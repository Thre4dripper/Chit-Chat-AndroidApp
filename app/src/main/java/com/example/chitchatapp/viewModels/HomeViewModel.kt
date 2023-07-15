package com.example.chitchatapp.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.chitchatapp.repository.AuthRepository
import com.example.chitchatapp.repository.HomeRepository
import com.example.chitchatapp.repository.UserDetailsRepository
import com.example.chitchatapp.store.UserDetails
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val userDetails = UserDetailsRepository.userDetails

    init {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            HomeRepository.getUsername { username ->
                if (username != null) {
                    UserDetails.saveUsername(application.applicationContext, username)
                }
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun signInUser(): Intent {
        return AuthRepository.signInUser()
    }

    fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
        AuthRepository.signOutUser(context, onSuccess)
    }

    fun onSignInResult(
        res: FirebaseAuthUIAuthenticationResult,
        onSuccess: (Boolean) -> Unit,
    ) {
        AuthRepository.onSignInResult(res, onSuccess)
    }

    fun checkInitialRegistration(
        onSuccess: (Boolean) -> Unit,
    ) =
        HomeRepository.checkInitialRegistration(onSuccess)
}