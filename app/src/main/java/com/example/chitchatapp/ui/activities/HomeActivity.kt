package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.HomeChatsRecyclerAdapter
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

    private lateinit var homeChatsAdapter: HomeChatsRecyclerAdapter

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
        initHomeFabs()
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

            //this will handle get wont be fetched when user is initial
            if (!isInitial) getChats()
        }
    }

    private fun initHomeFabs() {
        //initially hide all the fabs and text views
        binding.homeActionFab.shrink()
        binding.homeAddChatFab.hide()
        binding.homeAddGroupFab.hide()
        binding.homeAddChatFabTv.visibility = View.GONE
        binding.homeAddGroupFabTv.visibility = View.GONE

        binding.homeActionFab.icon = ContextCompat.getDrawable(this, R.drawable.ic_add)

        binding.homeActionFab.setOnClickListener {
            if (binding.homeAddChatFab.isOrWillBeHidden) {
                binding.homeAddChatFab.show()
                binding.homeAddGroupFab.show()
                binding.homeActionFab.extend()
                binding.homeAddChatFabTv.visibility = View.VISIBLE
                binding.homeAddGroupFabTv.visibility = View.VISIBLE

                binding.homeActionFab.icon = ContextCompat.getDrawable(this, R.drawable.ic_close)
            } else {
                binding.homeAddChatFab.hide()
                binding.homeAddGroupFab.hide()
                binding.homeActionFab.shrink()
                binding.homeAddChatFabTv.visibility = View.GONE
                binding.homeAddGroupFabTv.visibility = View.GONE

                binding.homeActionFab.icon = ContextCompat.getDrawable(this, R.drawable.ic_add)
            }
        }

        binding.homeAddChatFab.setOnClickListener {
            startActivity(Intent(this, AddChatsActivity::class.java))
        }

        //Todo
//        binding.homeAddGroupFab.setOnClickListener {
//            startActivity(Intent(this, AddGroupActivity::class.java))
//        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.homeAddChatFab.isOrWillBeShown)
            binding.homeActionFab.performClick()
        else
            super.onBackPressed()
    }

    /**
     * This function will be called when user sign out from the app
     */
    private fun signOut() {
        MaterialAlertDialogBuilder(this).setTitle("Sign Out")
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
            }.setNegativeButton("No") { _, _ -> }.show()
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
        viewModel.userDetails.observe(this) {
            if (it == null) return@observe
            binding.homeProfileImageProgressBar.visibility = View.GONE
            Glide.with(this).load(it.profileImage).placeholder(R.drawable.ic_profile).circleCrop()
                .into(binding.profileImageBtn)
        }
    }

    /**
     * This function will be called when sign in failed
     */
    private fun showSignInFailedDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Sign In Failed")
            .setMessage("Sign in failed. Do you want to try again?").setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                //restart activity
                finish()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }.setNegativeButton("No") { _, _ ->
                //close app
                finish()
            }.show()
    }

    private fun getChats() {
        binding.loadingLottie.visibility = View.VISIBLE

        viewModel.homeChats.observe(this) {
            if (it != null) {
                homeChatsAdapter.submitList(it)

                binding.addChatsLl.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                binding.loadingLottie.visibility = View.GONE
            }
        }

        viewModel.userDetails.observe(this) {
            if (it == null) return@observe

            binding.homeChatRv.apply {
                homeChatsAdapter = HomeChatsRecyclerAdapter(it.username)
                addItemDecoration(
                    DividerItemDecoration(
                        this@HomeActivity,
                        DividerItemDecoration.VERTICAL
                    )
                )
                adapter = homeChatsAdapter
            }

            //get chats when user details are fetched
            viewModel.getChats(this)
        }
    }
}