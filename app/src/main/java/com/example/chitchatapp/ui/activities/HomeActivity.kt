package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.firebase.Auth
import com.example.chitchatapp.firebase.firestore.FirestoreUtils
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

        binding.profileImage.setOnClickListener {
            startActivity(Intent(this, UserDetailsActivity::class.java))
        }
        binding.logoutBtn.setOnClickListener {
            Auth.signOut(this)
        }

        binding.loadingLottie.visibility = View.VISIBLE
        initCompleteProfileLayout(binding)
    }

    private fun initCompleteProfileLayout(binding: ActivityHomeBinding) {
        //do nothing, after sign in this func will be called again
        if (auth.currentUser == null) {
            binding.completeProfileLl.visibility = View.GONE
            return
        }

        //initially setting visibility to gone
        binding.completeProfileLl.visibility = View.GONE
        binding.completeProfileBtn.setOnClickListener {
            startActivity(Intent(this, UserDetailsActivity::class.java))
        }

        //checking if user has completed profile or not
        FirestoreUtils.checkInitialRegisteredUser(auth.currentUser!!) {
            binding.completeProfileLl.visibility = if (it) View.VISIBLE else View.GONE
            binding.loadingLottie.visibility = View.GONE
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Auth.onSignInResult(this, res, binding)

        //after sign in, initCompleteProfile will be called again
        initCompleteProfileLayout(binding)
    }
}