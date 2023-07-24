package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityGroupChatBinding
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.viewModels.GroupChatViewModel

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var viewModel: GroupChatViewModel

    private lateinit var groupId: String

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)
        viewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]

        //getting intent data
        groupId = intent.getStringExtra(ChatConstants.GROUP_ID) ?: ""
        val userModel = intent.getSerializableExtra(UserConstants.USER_MODEL) as? UserModel

        val loggedInUsername = viewModel.getLoggedInUsername(this)
        if (loggedInUsername == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            finish()
        }

        //This activity can be opened from two places
        //1. From the chats screen
        //2. From the add chats screen
        //If it is opened from the chats screen, then the user model will be null
        //If it is opened from the add chats screen, then the user model will not be null
        if (userModel != null)
//            createNewChat(userModel, loggedInUsername!!)
        else {
            getChatDetails(groupId, loggedInUsername!!)
        }

        binding.groupChatBackBtn.setOnClickListener { finish() }
//        initMenu()
    }

    private fun getChatDetails(groupId: String, loggedInUsername: String) {
        //init the recycler view
//        initRecyclerView(chatId, loggedInUsername)
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
//                chattingAdapter.submitList(it.chatMessages) {
//                    //when the list is submitted, then update the seen status
//                    viewModel.updateSeen(this, chatId) {}
//                    //scroll to the bottom of the recycler view
//                    binding.chattingRv.smoothScrollToPosition(it.chatMessages.size - 1)
//                }
            }
        }
    }
}