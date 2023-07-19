package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]

        val chatId = intent.getStringExtra(ChatConstants.CHAT_ID) ?: ""
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME) ?: ""

        @Suppress("DEPRECATION")
        val userModel = intent.getSerializableExtra(UserConstants.USER_MODEL) as? UserModel

        //user model is null when the user is adding a new chat
        if (userModel != null)
            createNewChat(userModel)
        else
            getChatDetails(chatId, loggedInUsername)

        binding.chattingBackBtn.setOnClickListener { finish() }
        setMenu()
    }

    private fun setMenu() {
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
        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.getChatDetails(chatId).observe(this) {
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

                //init the recycler view
                binding.chattingRv.apply {
                    chattingAdapter = ChattingRecyclerAdapter(loggedInUsername, it)
                    adapter = chattingAdapter
                }
                chattingAdapter.submitList(it.chatMessages)
            }
        }
    }

    private fun createNewChat(userModel: UserModel) {
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
        }
    }
}