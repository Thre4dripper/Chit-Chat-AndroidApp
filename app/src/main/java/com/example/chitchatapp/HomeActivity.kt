package com.example.chitchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.firebase.Auth
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding

    private val auth = FirebaseAuth.getInstance()

    //navigation drawer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (auth.currentUser == null)
            signInLauncher.launch(Auth.googleSignIn())

        //setting profile image in app bar
        Glide.with(this)
            .load(auth.currentUser?.photoUrl)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.profileImage)

        binding.logoutBtn.setOnClickListener {
            Auth.signOut(this)
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Auth.onSignInResult(this, res, binding)
    }
}