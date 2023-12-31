package com.example.chitchatapp.repository

import android.content.Context
import android.content.Intent
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.example.chitchatapp.firebase.auth.FirebaseUISignIn
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    companion object {
        fun signInUser(): Intent {
            return FirebaseUISignIn.signIn()
        }

        fun signOutUser(context: Context, onSuccess: (Boolean) -> Unit) {
            FirebaseUISignIn.signOut(context, onSuccess)
        }

        fun onSignInResult(
            res: FirebaseAuthUIAuthenticationResult,
            onSuccess: (Boolean) -> Unit,
        ) {
            val firestore = FirebaseFirestore.getInstance()
            FirebaseUISignIn.onSignInResult(res) { firebaseUser ->
                if (firebaseUser != null) {
                    FireStoreRegister.registerInitialUser(
                        firestore,
                        firebaseUser,
                    ) { isRegistered ->
                        onSuccess(isRegistered)
                    }
                } else {
                    onSuccess(false)
                }
            }
        }
    }
}