package com.example.chitchatapp.repository

import android.content.Context
import android.content.Intent
import com.example.chitchatapp.firebase.auth.GoogleSignIn
import com.example.chitchatapp.firebase.auth.FireStoreRegister
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.firestore.FirebaseFirestore

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
            val firestore = FirebaseFirestore.getInstance()
            GoogleSignIn.onSignInResult(res) { firebaseUser ->
                if (firebaseUser != null) {
                    FireStoreRegister.registerInitialUser(firestore, firebaseUser) { isRegistered ->
                        onSuccess(isRegistered)
                    }
                } else {
                    onSuccess(false)
                }
            }
        }
    }
}