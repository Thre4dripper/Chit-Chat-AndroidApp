package com.example.chitchatapp.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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
import com.example.chitchatapp.viewModels.GroupChatViewModel
import com.example.chitchatapp.viewModels.GroupProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GroupProfileActivity : AppCompatActivity(), GroupProfileClickInterface {
    private lateinit var binding: ActivityGroupProfileBinding
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var viewModel: GroupProfileViewModel

    private lateinit var mediaAdapter: GroupProfileMediaRecyclerAdapter
    private lateinit var groupMembersAdapter: GroupMembersRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile)
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]
        viewModel = ViewModelProvider(this)[GroupProfileViewModel::class.java]

        binding.groupProfileBackBtn.setOnClickListener {
            finish()
        }

        val groupId = intent.getStringExtra(GroupConstants.GROUP_ID)
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)
        initMediaRecycleView()
        initGroupMembersRecyclerView(groupId!!, loggedInUsername!!)
        getGroupDetails(groupId)
        initLeaveGroupButton(groupId)
    }

    private fun getGroupDetails(groupId: String) {
        groupChatViewModel.getLiveGroupChatDetails(groupId).observe(this) {
            if (it == null) return@observe

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_group)
                .circleCrop()
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
            MaterialAlertDialogBuilder(this)
                .setTitle("Leave Group")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Yes") { _, _ ->
                    groupChatViewModel.exitGroup(this, groupId) {
                        setResult(Constants.EXIT_GROUP)
                        finish()
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onMediaImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, groupMessageModel.image)
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                chatImageIv,
                getString(R.string.chatting_activity_chat_image_transition)
            )

        startActivity(intent, activityOptionsCompat.toBundle())
    }

    override fun onGroupMemberClicked(loggedInUsername: String, memberUsername: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        viewModel.findChatId(loggedInUsername, memberUsername) { chatId ->
            progressDialog.dismiss()
            if (chatId == null) {
                Toast.makeText(
                    this,
                    "Something went wrong. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
                return@findChatId
            }

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatConstants.CHAT_ID, chatId)
            startActivity(intent)
        }
    }
}