package com.example.chitchatapp.firebase

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import com.bumptech.glide.Glide
import com.example.chitchatapp.ui.HomeActivity
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.firebase.firestore.RegisterUser
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            MaterialAlertDialogBuilder(activity)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes") { _, _ ->
                    AuthUI.getInstance()
                        .signOut(activity)
                        .addOnCompleteListener {
                            activity.finish()
                        }
                }
                .setNegativeButton("No") { _, _ -> }
                .show()

        }

        fun onSignInResult(
            activity: Activity,
            result: FirebaseAuthUIAuthenticationResult,
            binding: ActivityHomeBinding
        ) {
            val response = result.idpResponse
            Log.d(TAG, "onSignInResult: $response")
            if (result.resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser

                //update profile image in app bar after sign in
                Glide.with(activity.applicationContext)
                    .load(user?.photoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.profileImage)

                //register user in firestore
                RegisterUser.registerInitialUser(user)
            } else {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("Sign In Failed")
                    .setMessage("Sign in failed. Do you want to try again?")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { _, _ ->
                        activity.finish()
                        activity.startActivity(
                            Intent(activity, HomeActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                    }
                    .setNegativeButton("No") { _, _ ->
                        activity.finish()
                    }
                    .show()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}