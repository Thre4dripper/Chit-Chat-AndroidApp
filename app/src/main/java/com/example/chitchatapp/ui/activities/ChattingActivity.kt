package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.ChattingRecyclerAdapter
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChattingBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.viewModels.ChattingViewModel

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private lateinit var viewModel: ChattingViewModel

    private lateinit var chattingAdapter: ChattingRecyclerAdapter

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]

        //getting intent data
        val chatId = intent.getStringExtra(ChatConstants.CHAT_ID) ?: ""
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
            createNewChat(userModel, loggedInUsername!!)
        else {
            getChatDetails(chatId, loggedInUsername!!)
        }

        binding.chattingBackBtn.setOnClickListener { finish() }
        initMenu()
    }

    private fun initMenu() {
        binding.chattingMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.chatting_screen_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_view_contact -> {
                        //TODO: open the contact details screen
                    }

                    R.id.action_clear_chat -> {
                        //TODO: clear the chat
                    }

                    R.id.action_delete_chat -> {
                        //TODO: delete the chat
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        //init the recycler view
        val chatModel = viewModel.getChatDetails(chatId)
        binding.chattingRv.apply {
            chattingAdapter = ChattingRecyclerAdapter(loggedInUsername, chatModel!!)
            adapter = chattingAdapter
        }
        initSendingLayout(chatId)

        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.getLiveChatDetails(chatId).observe(this) {
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

                //submit the live list to the adapter
                chattingAdapter.submitList(it.chatMessages) {
                    //when the list is submitted, then update the seen status
                    viewModel.updateSeen(this, chatId) {}
                    //scroll to the bottom of the recycler view
                    binding.chattingRv.scrollToPosition(it.chatMessages.size - 1)
                }
            }
        }
    }

    private fun createNewChat(userModel: UserModel, loggedInUsername: String) {
        binding.loadingLottie.visibility = View.VISIBLE

        //set the header with available details so far
        binding.chattingUsername.text = userModel.username
        Glide
            .with(this)
            .load(userModel.profileImage)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.chattingProfileImage)

        viewModel.createNewChat(userModel) {
            binding.loadingLottie.visibility = View.GONE

            //this can only be null when there is an error adding chat
            if (it == null) {
                Toast.makeText(this, "Error adding chat", Toast.LENGTH_SHORT).show()
                finish()
            }
            //otherwise if chat already exists then it will be that chat id
            getChatDetails(it!!, loggedInUsername)
        }
    }

    private fun initSendingLayout(chatId: String) {
        binding.sendMessageBtn.alpha = 0.5f
        binding.sendMessageBtn.isEnabled = false
        binding.sendMessageEt.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                binding.sendMessageBtn.alpha = 0.5f
                binding.sendMessageBtn.isEnabled = false
            } else {
                binding.sendMessageBtn.alpha = 1f
                binding.sendMessageBtn.isEnabled = true
            }
        }

        binding.sendMessageBtn.setOnClickListener {
            val message = binding.sendMessageEt.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message, chatId)
                binding.sendMessageEt.text?.clear()
            }
        }
    }

    private fun sendMessage(message: String, chatId: String) {
        viewModel.sendTextMessage(this, chatId, message) {
            if (it == null) {
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}