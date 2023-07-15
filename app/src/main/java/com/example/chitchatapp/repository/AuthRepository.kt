package com.example.chitchatapp.repository

import android.content.Context
import android.content.Intent
import com.example.chitchatapp.firebase.auth.GoogleSignIn
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class AuthRepository {
    companion object {
        fun signInUser(): Intent {
            return GoogleSignIn.googleSignIn()
        }

        fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
            GoogleSignIn.signOut(context) {
                onSuccess(it)
            }
        }

        fun onSignInResult(
            res: FirebaseAuthUIAuthenticationResult,
            onSuccess: (Boolean) -> Unit,
        ) {
            GoogleSignIn.onSignInResult(res) { it ->
                if (it != null) {
                    FireStoreRegister.registerInitialUser(it) { isRegistered ->
                        onSuccess(isRegistered)
                    }
                } else {
                    onSuccess(false)
                }
            }
        }
    }
}