package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChatProfileBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.viewModels.ChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatProfileBinding
    private lateinit var viewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_profile)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        binding.chatProfileBackBtn.setOnClickListener {
            finish()
        }


        val chatId = intent.getStringExtra(ChatConstants.CHAT_ID)
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)
        getChatDetails(chatId!!, loggedInUsername!!)
        initBottomButtons(chatId, loggedInUsername)
    }

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        viewModel.getLiveChatDetails(chatId).observe(this) { chatModel ->
            if (chatModel == null) return@observe

            val profileImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(this).load(profileImage).placeholder(R.drawable.ic_profile).circleCrop()
                .into(binding.chatProfileProfileIv)

            val username = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.chatProfileUsernameTv.text = username
        }
    }

    private fun initBottomButtons(chatId: String, loggedInUsername: String) {
        initFavouriteBtn(chatId, loggedInUsername)
        binding.chatProfileClearChatBtn.setOnClickListener {
            clearChat(chatId)
        }
        binding.chatProfileDeleteChatBtn.setOnClickListener {
            deleteChat(chatId)
        }
    }

    private fun initFavouriteBtn(chatId: String, loggedInUsername: String) {
        viewModel.listenChatIsFavorite(loggedInUsername) { userModel ->
            if (userModel == null) return@listenChatIsFavorite

            val isFavorite = userModel.favourites.contains(chatId)
            binding.chatProfileFavoriteBtn.text = if (isFavorite) "UnFavorite" else "Favourite"
            if (isFavorite) {
                binding.chatProfileFavoriteBtn.setCompoundDrawables(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.ic_unfavourite, null
                    ), null, null, null
                )
            } else {
                binding.chatProfileFavoriteBtn.setCompoundDrawables(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.ic_favourite, null
                    ), null, null, null
                )
            }

            binding.chatProfileFavoriteBtn.setOnClickListener {
                viewModel.favouriteChat(userModel, chatId) {
                    if (it == null) return@favouriteChat
                    Toast.makeText(
                        this, if (!userModel.favourites.contains(chatId)) "Marked as Favourite"
                        else "Clear Favourite", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearChat(chatId: String) {
        clearOrDeleteDialog(
            "Clear chat",
            "Are you sure you want to clear this chat?"
        ) {
            viewModel.clearChat(chatId) {
                Toast.makeText(
                    this,
                    if (it) "Chat cleared" else "Error clearing chat",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun deleteChat(chatId: String) {
        clearOrDeleteDialog(
            "Delete chat",
            "Are you sure you want to delete this chat?"
        ) {
            viewModel.deletedChat(chatId) {
                Toast.makeText(
                    this,
                    if (it) "Chat deleted" else "Error deleting chat",
                    Toast.LENGTH_SHORT
                ).show()
                setResult(Constants.DELETE_CHAT)
                finish()
            }
        }
    }

    private fun clearOrDeleteDialog(title: String, message: String, action: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                action()
            }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }
}