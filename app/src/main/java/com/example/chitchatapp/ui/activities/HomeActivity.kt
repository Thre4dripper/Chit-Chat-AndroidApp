package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.HomeChatsRecyclerAdapter
import com.example.chitchatapp.adapters.HomeFavRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.ChatClickInterface
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityHomeBinding
import com.example.chitchatapp.enums.FragmentType
import com.example.chitchatapp.viewModels.HomeViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeActivity : AppCompatActivity(), ChatClickInterface {
    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var homeChatsAdapter: HomeChatsRecyclerAdapter
    private lateinit var homeFavChatsAdapter: HomeFavRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.root.fitsSystemWindows = true
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (viewModel.getCurrentUser() == null && !viewModel.isFirebaseUILaunched) {
            signInLauncher.launch(viewModel.signInUser())
        }

        //all the click listeners
        binding.profileImageBtn.setOnClickListener {
            val intent = Intent(this, UserDetailsActivity::class.java)
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.profileImageBtn,
                getString(R.string.user_details_activity_profile_image_transition)
            )
            startActivity(intent, activityOptionsCompat.toBundle())
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
        binding.homeLabelFavChatsTv.visibility = View.GONE
        binding.homeChatFavRv.visibility = View.GONE
        binding.homeLabelChatsTv.visibility = View.GONE

        //do nothing, after sign in this func will be called again
        if (viewModel.getCurrentUser() == null) return

        //notification permission
        initPermissions()

        binding.completeProfileBtn.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, FragmentType.FRAGMENT_USERNAME.name)
            startActivity(intent)
        }

        binding.addChatsBtn.setOnClickListener {
            startActivity(Intent(this, AddChatsActivity::class.java))
        }

        //checking if user has completed profile or not
        viewModel.checkUserRegistration { isInitial ->
            binding.loadingLottie.visibility = View.GONE
            binding.completeProfileLl.visibility = if (isInitial) View.VISIBLE else View.GONE

            //this will handle get wont be fetched when user is initial
            if (!isInitial) getChats()
        }
    }

    private fun initHomeFabs() {
        //hide action fab initially, this will be shown when chats are fetched
        binding.homeActionFab.visibility = View.GONE

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

        binding.homeAddGroupFab.setOnClickListener {
            val intent = Intent(this, AddGroupActivity::class.java)

            // username will be available
            // because this button will be shown only when user has completed and fetched profile
            intent.putExtra(UserConstants.USERNAME, viewModel.userDetails.value!!.username)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION") if (binding.homeAddChatFab.isOrWillBeShown) binding.homeActionFab.performClick()
        else super.onBackPressed()
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
                        startActivity(Intent(this, HomeActivity::class.java))
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
            // dont do anything if the activity is finishing or destroyed
            if (isFinishing && isDestroyed) return@onSignInResult
            if (it) {
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
            .setPositiveButton("Yes") { _, _ ->
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

        viewModel.homeChats.observe(this) { homeChats ->
            if (homeChats == null) return@observe
            if (this::homeChatsAdapter.isInitialized) {
                homeChatsAdapter.submitList(homeChats)
            }

            if (homeChats.isEmpty()) {
                binding.homeLabelChatsTv.visibility = View.GONE
                binding.homeChatRv.visibility = View.GONE
                binding.addChatsLl.visibility = View.VISIBLE
                binding.homeActionFab.visibility = View.GONE
            } else {
                binding.homeLabelChatsTv.visibility = View.VISIBLE
                binding.homeChatRv.visibility = View.VISIBLE
                binding.addChatsLl.visibility = View.GONE
                binding.homeActionFab.visibility = View.VISIBLE
            }

            //hiding loading lottie on loading complete
            binding.loadingLottie.visibility = View.GONE
        }

        viewModel.userDetails.observe(this) {
            if (it == null || it.username.isEmpty()) return@observe

            binding.homeChatRv.apply {
                homeChatsAdapter = HomeChatsRecyclerAdapter(it.username, this@HomeActivity)
                adapter = homeChatsAdapter
            }

            binding.homeChatFavRv.apply {
                homeFavChatsAdapter = HomeFavRecyclerAdapter(it.username, this@HomeActivity)
                adapter = homeFavChatsAdapter
            }

            //get chats when user details are fetched
            viewModel.getHomeChats(this)

            //listen for favourite chats in user details
            viewModel.listenFavChats(it.username) { userModel ->
                //visibility control for home label fav chats and home fav chat recycler view
                if (userModel == null) return@listenFavChats

                val hasFavourites = userModel.favourites.isNotEmpty()

                if (hasFavourites) {
                    binding.homeLabelFavChatsTv.visibility = View.VISIBLE
                    binding.homeChatFavRv.visibility = View.VISIBLE
                } else {
                    binding.homeLabelFavChatsTv.visibility = View.GONE
                    binding.homeChatFavRv.visibility = View.GONE
                }

                //filtering home chats for fav chats
                viewModel.homeChats.observe(this@HomeActivity) { homeChats ->
                    val favChats = homeChats?.filter { homeChat ->
                        userModel.favourites.contains(homeChat.userChat?.chatId)
                    }
                    homeFavChatsAdapter.submitList(favChats)
                }
            }
        }
    }

    override fun onChatClicked(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatConstants.CHAT_ID, chatId)
        intent.putExtra(Constants.PREV_ACTIVITY, true)
        startActivity(intent)
    }

    override fun onGroupChatClicked(groupId: String) {
        val intent = Intent(this, GroupChatActivity::class.java)
        intent.putExtra(GroupConstants.GROUP_ID, groupId)
        intent.putExtra(Constants.PREV_ACTIVITY, true)
        startActivity(intent)
    }

    private fun initPermissions() {
        //request notification permission whether it is granted or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                when {
                    //this should handle the case when user has denied the permission but not permanently
                    shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                        MaterialAlertDialogBuilder(this).setTitle("Notifications Permission")
                            .setMessage("Notifications permission is required to receive device notifications")
                            .setPositiveButton("Enable") { dialog, _ ->
                                dialog.dismiss()
                                initPermissions()
                            }.setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }.show()
                    }

                    //this should handle the case when user has permanently denied the permission
                    else -> {
                        MaterialAlertDialogBuilder(this).setTitle("Notifications Permission")
                            .setMessage("It seems like you have permanently denied notifications permission. You can enable it from settings")
                            .setPositiveButton("Settings") { dialog, _ ->
                                dialog.dismiss()
                                //open settings of the app
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }.setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }.show()
                    }
                }
            }
        }
}