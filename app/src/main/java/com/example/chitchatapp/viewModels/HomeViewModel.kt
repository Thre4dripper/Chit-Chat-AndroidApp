package com.example.chitchatapp.viewModels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.repository.AuthRepository
import com.example.chitchatapp.repository.UserDetailsRepository
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeViewModel : ViewModel() {
    val userDetails = UserDetailsRepository.userDetails

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
}