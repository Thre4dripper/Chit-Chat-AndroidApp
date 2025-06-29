package com.example.chitchatapp.firebase.auth

import android.app.Activity.RESULT_OK
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
            val response = result.idpResponse
            if (result.resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                callback(user)
            } else {
                callback(null)
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}