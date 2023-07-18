package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChattingBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.viewModels.ChattingViewModel

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private lateinit var viewModel: ChattingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]

        val chatId = intent.getStringExtra(ChatConstants.CHAT_ID) ?: ""
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME) ?: ""
        getChatDetails(chatId, loggedInUsername)
    }

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.chatDetails.observe(this) {
            if (it != null) {
                binding.loadingLottie.visibility = View.GONE

                val chatProfileImage = ChatUtils.getChatProfileImage(it, loggedInUsername)
                Glide
                    .with(this)
                    .load(chatProfileImage)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.chattingProfileImage)

                val headerUsername = ChatUtils.getChatUsername(it, loggedInUsername)

                binding.chattingUsername.text = headerUsername
            }
        }
        viewModel.getChatDetails(chatId)
    }
}