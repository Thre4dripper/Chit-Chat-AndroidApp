package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.viewModels.HomeViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (viewModel.getCurrentUser() == null && !viewModel.isFirebaseUILaunched) {
            signInLauncher.launch(viewModel.signInUser())
        }

        //all the click listeners
        binding.profileImageBtn.setOnClickListener {
            startActivity(Intent(this, UserDetailsActivity::class.java))
        }
        binding.logoutBtn.setOnClickListener {
            signOut()
        }

        binding.loadingLottie.visibility = View.VISIBLE
        setProfileImage()
        initCompleteProfileLayout(binding)
    }

    private fun initCompleteProfileLayout(binding: ActivityHomeBinding) {
        //do nothing, after sign in this func will be called again
        if (viewModel.getCurrentUser() == null) {
            binding.completeProfileLl.visibility = View.GONE
            return
        }

        //initially setting visibility to gone
        binding.completeProfileLl.visibility = View.GONE
        binding.completeProfileBtn.setOnClickListener {
            startActivity(Intent(this, UserDetailsActivity::class.java))
        }

        //checking if user has completed profile or not
        viewModel.checkInitialRegistration {
            binding.completeProfileLl.visibility = if (it) View.VISIBLE else View.GONE
            binding.loadingLottie.visibility = View.GONE
        }
    }

    /**
     * This function will be called when user sign out from the app
     */
    private fun signOut() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.signOutUser(this) {
                    if (it) {
                        finish()
                    } else {
                        dialog.dismiss()
                        signOut()
                    }
                }
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }

    /**
     * Intent launcher for sign in
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        viewModel.onSignInResult(res) {
            if (it) {
                setProfileImage()
                //after sign in, initCompleteProfile will be called again
                initCompleteProfileLayout(binding)
            } else {
                showSignInFailedDialog()
            }
        }
    }

    private fun setProfileImage() {
        Glide.with(this)
            .load(viewModel.getCurrentUser()?.photoUrl)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.profileImageBtn)
    }

    /**
     * This function will be called when sign in failed
     */
    private fun showSignInFailedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sign In Failed")
            .setMessage("Sign in failed. Do you want to try again?")
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                //restart activity
                finish()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                //close app
                finish()
            }
            .show()
    }
}