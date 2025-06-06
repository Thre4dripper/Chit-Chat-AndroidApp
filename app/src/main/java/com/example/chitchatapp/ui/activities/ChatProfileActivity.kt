package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.ChatProfileMediaRecyclerAdapter
import com.example.chitchatapp.adapters.CommonGroupsRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.ChatProfileClickInterface
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChatProfileBinding
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.viewModels.ChatProfileViewModel
import com.example.chitchatapp.viewModels.ChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatProfileActivity : AppCompatActivity(), ChatProfileClickInterface {
    private val TAG = "ChatProfileActivity"

    private lateinit var binding: ActivityChatProfileBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var viewModel: ChatProfileViewModel

    private lateinit var mediaAdapter: ChatProfileMediaRecyclerAdapter
    private lateinit var commonGroupsAdapter: CommonGroupsRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_profile)
        binding.root.fitsSystemWindows = true
        viewModel = ViewModelProvider(this)[ChatProfileViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.chatProfileBackBtn.setOnClickListener {
            supportFinishAfterTransition()
        }


        val chatId = intent.getStringExtra(ChatConstants.CHAT_ID)
        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)

        getChatDetails(chatId!!, loggedInUsername!!)
    }

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        chatViewModel.chatDetails.observe(this) { chatModel ->
            if (chatModel == null) return@observe

            //init everything after chat details are fetched
            initOpenChatButton(chatModel)
            initNameAndBio(chatModel, loggedInUsername)
            initMediaRecycleView()
            initMuteSwitch(chatModel, loggedInUsername)
            initCommonGroupsRecyclerView(loggedInUsername)
            initBottomButtons(chatId, loggedInUsername)

            val profileImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(this).load(profileImage).placeholder(R.drawable.ic_profile).circleCrop()
                .into(binding.chatProfileProfileIv)

            val username = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.chatProfileUsernameTv.text = username

            val chatMediasList = chatModel.chatMessages.filter { chatMessage ->
                chatMessage.type == ChatMessageType.TypeImage
            }

            if (chatMediasList.isEmpty()) {
                binding.chatProfileMediaRv.visibility = View.GONE
                binding.chatProfileNoMediaLottie.visibility = View.VISIBLE
            } else {
                binding.chatProfileMediaRv.visibility = View.VISIBLE
                binding.chatProfileNoMediaLottie.visibility = View.GONE
            }

            mediaAdapter.submitList(chatMediasList)
        }

        chatViewModel.getLiveChatDetails(chatId)
    }

    private fun initOpenChatButton(chatModel: ChatModel) {
        binding.chatProfileOpenChatBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatConstants.CHAT_ID, chatModel.chatId)
            intent.putExtra(Constants.PREV_ACTIVITY, true)

            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.chatProfileOpenChatBtn,
                getString(R.string.chat_activity_chat_profile_image_transition)
            )
            startActivity(intent, activityOptionsCompat.toBundle())
        }
    }

    private fun initNameAndBio(chatModel: ChatModel, loggedInUsername: String) {
        val chatUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)

        viewModel.getNameAndBio(chatUsername) { name, bio ->
            if (name.isEmpty()) {
                binding.chatProfileNameTv.visibility = View.GONE
            } else {
                binding.chatProfileNameTv.visibility = View.VISIBLE
                binding.chatProfileNameTv.text = name
            }

            if (bio.isEmpty()) {
                binding.chatProfileBioTv.visibility = View.GONE
            } else {
                binding.chatProfileBioTv.visibility = View.VISIBLE
                binding.chatProfileBioTv.text = bio
            }
        }

    }

    private fun initMediaRecycleView() {
        //do not initialize again if already initialized
        if (this::mediaAdapter.isInitialized) return

        binding.chatProfileMediaRv.apply {
            mediaAdapter = ChatProfileMediaRecyclerAdapter(this@ChatProfileActivity)
            adapter = mediaAdapter
        }
    }

    private fun initMuteSwitch(chatModel: ChatModel, loggedInUsername: String) {
        val isMuted = chatModel.mutedBy.contains(loggedInUsername)
        binding.chatProfileMuteSwitch.isChecked = isMuted

        binding.chatProfileMuteSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.muteUnMuteChat(chatModel, loggedInUsername, isChecked) {
                Toast.makeText(
                    this, if (isChecked) "Muted" else "UnMuted", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initCommonGroupsRecyclerView(loggedInUsername: String) {
        //do not initialize again if already initialized
        if (this::commonGroupsAdapter.isInitialized) return

        val chatModel = chatViewModel.chatDetails.value ?: return
        val chatUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
        val chatUserImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)

        binding.chatProfileGroupsRv.apply {
            commonGroupsAdapter = CommonGroupsRecyclerAdapter(this@ChatProfileActivity)
            adapter = commonGroupsAdapter
        }

        viewModel.commonGroups(chatUsername, chatUserImage, loggedInUsername) {
            if (it.isEmpty()) {
                binding.chatProfileNoGroupsTv.visibility = View.VISIBLE
                binding.chatProfileGroupsRv.visibility = View.GONE
            } else {
                binding.chatProfileNoGroupsTv.visibility = View.GONE
                binding.chatProfileGroupsRv.visibility = View.VISIBLE
            }
            commonGroupsAdapter.submitList(it)
        }
    }

    private fun initBottomButtons(chatId: String, loggedInUsername: String) {
        initFavouriteBtn(chatId, loggedInUsername)
        binding.chatProfileClearChatBtn.setOnClickListener {
            clearChat()
        }
        binding.chatProfileDeleteChatBtn.setOnClickListener {
            deleteChat()
        }
    }

    private fun initFavouriteBtn(chatId: String, loggedInUsername: String) {
        chatViewModel.listenChatIsFavorite(loggedInUsername) { userModel ->
            if (userModel == null) return@listenChatIsFavorite

            val isFavorite = userModel.favourites.contains(chatId)
            binding.chatProfileFavoriteBtn.text = if (isFavorite) "UnFavorite" else "Favourite"
            if (isFavorite) {
                binding.chatProfileFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.ic_favourite, null
                    ), null, null, null
                )
            } else {
                binding.chatProfileFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.ic_unfavourite, null
                    ), null, null, null
                )
            }

            binding.chatProfileFavoriteBtn.setOnClickListener {
                chatViewModel.favouriteChat(userModel, chatId) {
                    if (it == null) return@favouriteChat
                    Toast.makeText(
                        this, if (!userModel.favourites.contains(chatId)) "Marked as Favourite"
                        else "Clear Favourite", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun clearChat() {
        clearOrDeleteDialog(
            "Clear chat", "Are you sure you want to clear this chat?"
        ) {
            chatViewModel.clearChat {
                Toast.makeText(
                    this, if (it) "Chat cleared" else "Error clearing chat", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun deleteChat() {
        clearOrDeleteDialog(
            "Delete chat", "Are you sure you want to delete this chat?"
        ) {
            chatViewModel.deletedChat {
                Toast.makeText(
                    this, if (it) "Chat deleted" else "Error deleting chat", Toast.LENGTH_SHORT
                ).show()
                setResult(Constants.DELETE_CHAT)
                finish()
            }
        }
    }

    private fun clearOrDeleteDialog(title: String, message: String, action: () -> Unit) {
        MaterialAlertDialogBuilder(this).setTitle(title).setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                action()
            }.setNegativeButton("No") { _, _ -> }.show()
    }

    override fun onMediaImageClicked(chatMessageModel: ChatMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, chatMessageModel.image)
        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, chatImageIv, getString(R.string.chatting_activity_chat_image_transition)
        )

        startActivity(intent, activityOptionsCompat.toBundle())
    }

    override fun onCommonGroupClicked(groupId: String, clickedIv: ImageView) {
        val intent = Intent(this, GroupChatActivity::class.java)
        intent.putExtra(GroupConstants.GROUP_ID, groupId)
        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, clickedIv, getString(R.string.group_chat_activity_group_profile_image_transition)
        )
        startActivity(intent, activityOptionsCompat.toBundle())
    }
}