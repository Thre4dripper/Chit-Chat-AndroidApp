package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.GroupMembersRecyclerAdapter
import com.example.chitchatapp.adapters.GroupProfileMediaRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.GroupProfileClickInterface
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityGroupProfileBinding
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.models.GroupMessageModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.viewModels.GroupChatViewModel
import com.example.chitchatapp.viewModels.GroupProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GroupProfileActivity : AppCompatActivity(), GroupProfileClickInterface {
    private lateinit var binding: ActivityGroupProfileBinding
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var viewModel: GroupProfileViewModel

    private lateinit var mediaAdapter: GroupProfileMediaRecyclerAdapter
    private lateinit var groupMembersAdapter: GroupMembersRecyclerAdapter

    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile)
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]
        viewModel = ViewModelProvider(this)[GroupProfileViewModel::class.java]

        binding.groupProfileBackBtn.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }

        groupId = intent.getStringExtra(GroupConstants.GROUP_ID)
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)

        initSetGroupImageBtn()
        initMediaRecycleView()
        initGroupMembersRecyclerView(groupId!!, loggedInUsername!!)
        getGroupDetails(groupId!!)
        initLeaveGroupButton(groupId!!)
    }

    private fun getGroupDetails(groupId: String) {
        binding.groupProfileImageProgressBar.visibility = View.VISIBLE
        groupChatViewModel.getLiveGroupChatDetails(groupId).observe(this) {
            if (it == null) return@observe

            binding.groupProfileImageProgressBar.visibility = View.GONE
            Glide.with(this).load(it.image).placeholder(R.drawable.ic_group).circleCrop()
                .into(binding.groupProfileProfileIv)

            binding.groupProfileNameTv.text = it.name

            val chatMediasList = it.messages.filter { chatMessageModel ->
                chatMessageModel.type == GroupMessageType.TypeImage
            }

            if (chatMediasList.isEmpty()) {
                binding.groupProfileMediaRv.visibility = View.GONE
                binding.groupProfileNoMediaLottie.visibility = View.VISIBLE
            } else {
                binding.groupProfileMediaRv.visibility = View.VISIBLE
                binding.groupProfileNoMediaLottie.visibility = View.GONE
            }

            mediaAdapter.submitList(chatMediasList)
        }
    }

    private fun initSetGroupImageBtn() {
        binding.groupProfileImageProgressBar.visibility = View.GONE
        binding.groupProfileSetProfileImage.setOnClickListener {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    /**
     * Registers a photo picker activity launcher in single-select mode.
     */
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia())
        { pickedPhotoUri ->
            if (pickedPhotoUri != null) {
                var uri: Uri? = null

                //async call to create temp file
                CoroutineScope(Dispatchers.Main).launch {
                    uri = Uri.fromFile(withContext(Dispatchers.IO) {
                        val file = File.createTempFile( //creating temp file
                            "temp", ".jpg", cacheDir
                        )
                        file
                    })
                }
                    //on completion of async call
                    .invokeOnCompletion {
                        //Crop activity with source and destination uri
                        val uCrop = UCrop.of(pickedPhotoUri, uri!!).withAspectRatio(1f, 1f)
                            .withMaxResultSize(1080, 1080)

                        cropImageCallback.launch(uCrop.getIntent(this))
                    }

            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    /**
     * CALLBACK FOR CROPPING RECEIVED IMAGE
     */
    private var cropImageCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uCropResult ->
            if (uCropResult.resultCode == RESULT_OK) {
                val imageUri = UCrop.getOutput(uCropResult.data!!)!!

                binding.groupProfileImageProgressBar.visibility = View.VISIBLE
                viewModel.updateGroupImage(imageUri, groupId!!) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun initMediaRecycleView() {
        binding.groupProfileMediaRv.apply {
            mediaAdapter = GroupProfileMediaRecyclerAdapter(this@GroupProfileActivity)
            adapter = mediaAdapter
        }
    }

    private fun initGroupMembersRecyclerView(groupId: String, loggedInUsername: String) {
        val groupChatModel = groupChatViewModel.getGroupChatDetails(groupId) ?: return

        binding.groupProfileMembersRv.apply {
            groupMembersAdapter =
                GroupMembersRecyclerAdapter(loggedInUsername, this@GroupProfileActivity)
            adapter = groupMembersAdapter
        }

        groupMembersAdapter.submitList(groupChatModel.members)
    }

    private fun initLeaveGroupButton(groupId: String) {
        binding.groupProfileExitGroupBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Leave Group")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Yes") { _, _ ->
                    groupChatViewModel.exitGroup(this, groupId) {
                        setResult(Constants.EXIT_GROUP)
                        finish()
                    }
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    override fun onMediaImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, groupMessageModel.image)
        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, chatImageIv, getString(R.string.chatting_activity_chat_image_transition)
        )

        startActivity(intent, activityOptionsCompat.toBundle())
    }

    override fun onGroupMemberClicked(loggedInUsername: String, memberUsername: String) {
        val loaderDialog =
            MaterialAlertDialogBuilder(this).setView(R.layout.dialog_loader).setCancelable(false)
                .show()

        findChat(loggedInUsername, memberUsername) { chatId ->
            if (chatId != null) {
                loaderDialog.dismiss()
                return@findChat
            }

            findUser(memberUsername) {
                if (it != null) {
                    loaderDialog.dismiss()
                    return@findUser
                }

                Toast.makeText(this, "Error Fetching User details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findChat(
        loggedInUsername: String,
        memberUsername: String,
        onSuccess: (String?) -> Unit,
    ) {
        viewModel.findChatId(loggedInUsername, memberUsername) { chatId ->
            if (chatId == null) {
                onSuccess(null)
                return@findChatId
            }

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatConstants.CHAT_ID, chatId)
            startActivity(intent)
            onSuccess(chatId)
        }
    }

    private fun findUser(
        memberUsername: String, onSuccess: (UserModel?) -> Unit
    ) {
        viewModel.getUserModel(memberUsername) { userModel ->
            if (userModel == null) {
                onSuccess(null)
                return@getUserModel
            }

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(UserConstants.USER_MODEL, userModel)
            startActivity(intent)
            onSuccess(userModel)
        }
    }
}