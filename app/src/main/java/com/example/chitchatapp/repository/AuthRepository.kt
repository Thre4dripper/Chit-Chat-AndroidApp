package com.example.chitchatapp.repository

import android.content.Context
import android.content.Intent
import com.example.chitchatapp.firebase.Auth
import com.example.chitchatapp.firebase.firestore.RegisterUser
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class AuthRepository {
    companion object {
        fun signInUser(): Intent {
            return Auth.googleSignIn()
        }

        fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
            Auth.signOut(context) {
                onSuccess(it)
            }
        }

        fun onSignInResult(
            res: FirebaseAuthUIAuthenticationResult,
            onSuccess: (Boolean) -> Unit,
        ) {
            Auth.onSignInResult(res) { it ->
                if (it != null) {
                    RegisterUser.registerInitialUser(it) { isRegistered ->
                        onSuccess(isRegistered)
                    }
                } else {
                    onSuccess(false)
                }
            }
        }
    }
}