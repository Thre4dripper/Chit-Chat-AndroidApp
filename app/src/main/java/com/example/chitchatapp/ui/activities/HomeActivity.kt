package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.enums.FragmentType
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
        initHomeLottieLayouts()
    }

    private fun initHomeLottieLayouts() {
        //hide all the layouts
        binding.completeProfileLl.visibility = View.GONE
        binding.addChatsLl.visibility = View.GONE

        //do nothing, after sign in this func will be called again
        if (viewModel.getCurrentUser() == null) return

        binding.completeProfileBtn.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, FragmentType.FRAGMENT_USERNAME.name)
            startActivity(intent)
        }

        binding.addChatsBtn.setOnClickListener {
            startActivity(Intent(this, AddChatsActivity::class.java))
        }

        //checking if user has completed profile or not
        viewModel.checkInitialRegistration { isInitial ->
            binding.loadingLottie.visibility = View.GONE
            binding.completeProfileLl.visibility = if (isInitial) View.VISIBLE else View.GONE
            if (!isInitial) getChats()
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
                initHomeLottieLayouts()
            } else {
                showSignInFailedDialog()
            }
        }
    }

    private fun setProfileImage() {
        binding.homeProfileImageProgressBar.visibility = View.VISIBLE
        viewModel.profileImage.observe(this) {
            binding.homeProfileImageProgressBar.visibility = View.GONE
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.profileImageBtn)
        }
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

    private fun getChats() {
        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.homeChats.observe(this) {
            Log.d(TAG, "getChats: $it")
            if (it != null) {
                binding.addChatsLl.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                binding.loadingLottie.visibility = View.GONE
            }
        }

        viewModel.getChats(this)
    }
}