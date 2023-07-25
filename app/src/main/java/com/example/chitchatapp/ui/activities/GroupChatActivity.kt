package com.example.chitchatapp.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.GroupChatRecyclerAdapter
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.databinding.ActivityGroupChatBinding
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.viewModels.AddGroupViewModel
import com.example.chitchatapp.viewModels.GroupChatViewModel

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var viewModel: GroupChatViewModel

    private lateinit var groupChatAdapter: GroupChatRecyclerAdapter
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)
        viewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]

        //getting intent data
        groupId = intent.getStringExtra(ChatConstants.GROUP_ID)
        val groupName = intent.getStringExtra(GroupConstants.GROUP_NAME)
        val groupImage = intent.getStringExtra(GroupConstants.GROUP_IMAGE)
        val groupMembers = AddGroupViewModel.selectedUsers.value

        val loggedInUsername = viewModel.getLoggedInUsername(this)
        if (loggedInUsername == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
        }

        //This activity can be opened from two places
        //1. From the chats screen
        //2. From the add group screen
        //If it is opened from the chats screen, then groupId will not be null
        if (groupId == null)
            createNewGroup(
                groupName!!,
                groupImage,
                groupMembers!!,
                loggedInUsername!!
            )
        else {
            getChatDetails(groupId!!, loggedInUsername!!)
        }

        binding.groupChatBackBtn.setOnClickListener { finish() }
//        initMenu()
    }

    private fun getChatDetails(groupId: String, loggedInUsername: String) {
        //init the recycler view
        initRecyclerView(groupId, loggedInUsername)
//        initSendingLayout(chatId)

        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.getLiveGroupChatDetails(groupId).observe(this) {
            if (it != null) {
                binding.loadingLottie.visibility = View.GONE

                //setting the group image
                Glide
                    .with(this)
                    .load(it.image)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.groupChatGroupImage)

                //setting the group name
                binding.groupChatGroupName.text = it.name

//                //submit the live list to the adapter
                groupChatAdapter.submitList(it.messages) {
                    //when the list is submitted, then update the seen status
//                    viewModel.updateSeen(this, chatId) {}
                    //scroll to the bottom of the recycler view
                    binding.groupChatRv.smoothScrollToPosition(it.messages.size - 1)
                }
            }
        }
    }

    private fun createNewGroup(
        groupName: String,
        groupImage: String?,
        groupMembers: List<ChatModel>,
        loggedInUsername: String
    ) {
        binding.loadingLottie.visibility = View.VISIBLE

        //set the header with available details so far
        binding.groupChatGroupName.text = groupName
        Glide
            .with(this)
            .load(groupImage)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.groupChatGroupImage)

        val groupImageUri = if (groupImage == null) null else Uri.parse(groupImage)
        viewModel.createGroup(this, groupName, groupImageUri, groupMembers) {
            binding.loadingLottie.visibility = View.GONE

            //this can only be null when there is an error adding chat
            if (it == null) {
                Toast.makeText(this, "Error creating group", Toast.LENGTH_SHORT).show()
                finish()
            }
            //otherwise if chat already exists then it will be that chat id
            getChatDetails(it!!, loggedInUsername)
        }
    }

    private fun initRecyclerView(groupId: String, loggedInUsername: String) {
        val groupChatModel = viewModel.getGroupChatDetails(groupId)
        binding.groupChatRv.apply {
            groupChatAdapter =
                GroupChatRecyclerAdapter()
            adapter = groupChatAdapter
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                //scroll to the bottom of the recycler view on keyboard open
                if (bottom < oldBottom) {
                    binding.groupChatRv.smoothScrollToPosition(groupChatModel!!.messages.size - 1)
                }
            }
        }
    }
}