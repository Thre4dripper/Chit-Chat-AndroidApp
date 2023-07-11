package com.example.chitchatapp.firebase

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class Auth {
    companion object {
        private const val TAG = "Auth"
        fun googleSignIn(): Intent {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
            )

            return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAlwaysShowSignInMethodScreen(true)
                .setAvailableProviders(providers)
                .build()
        }

        fun signOut(activity: Activity) {
            AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener {
                    activity.finish()
                }
        }

        fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
            val response = result.idpResponse
            if (result.resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                //TODO handle this

                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}