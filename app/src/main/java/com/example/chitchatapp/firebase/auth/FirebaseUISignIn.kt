package com.example.chitchatapp.firebase.auth

import android.content.Context
import android.content.Intent
import com.example.chitchatapp.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUISignIn {
    companion object {
        private const val TAG = "Auth"
        fun signIn(): Intent {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.GitHubBuilder().build(),
            )

            return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAlwaysShowSignInMethodScreen(true)
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_ChitChat)
                .setLogo(R.drawable.auth_ui_logo)
                .build()
        }

        fun signOut(context: Context, onSuccess: (Boolean) -> Unit) {
            AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener {
                    onSuccess(it.isSuccessful)
                }.addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun onSignInResult(
            result: FirebaseAuthUIAuthenticationResult,
            callback: (user: FirebaseUser?) -> Unit,
        ) {
            val auth = FirebaseAuth.getInstance()
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    callback(user)
                } else {
                    callback(null)
                }
            }

            auth.addAuthStateListener(listener)
        }
    }
}