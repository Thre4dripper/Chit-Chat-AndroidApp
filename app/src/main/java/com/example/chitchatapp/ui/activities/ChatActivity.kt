package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.ChattingRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.ChatMessageClickInterface
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChattingBinding
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.ui.bottomSheet.StickersBottomSheet
import com.example.chitchatapp.viewModels.ChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ChatActivity : AppCompatActivity(), ChatMessageClickInterface {
    private val TAG = "ChattingActivity"

    private lateinit var binding: ActivityChattingBinding
    private lateinit var viewModel: ChatViewModel

    private lateinit var chattingAdapter: ChattingRecyclerAdapter
    private lateinit var chatId: String

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        //getting intent data
        chatId = intent.getStringExtra(ChatConstants.CHAT_ID) ?: ""
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
    }

    private fun initMenu(chatId: String, loggedInUsername: String) {
        viewModel.listenChatIsFavorite(loggedInUsername) { userModel ->

            binding.chattingMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.menuInflater.inflate(R.menu.chatting_screen_menu, popupMenu.menu)

                val favItem = popupMenu.menu.findItem(R.id.action_favorite)
                val isFavourite = userModel?.favourites?.contains(chatId)
                favItem.title = if (isFavourite == true) "UnFavourite" else "Favourite"

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_view_contact -> openProfile(chatId, loggedInUsername)
                        R.id.action_favorite -> markFavourite(userModel!!, chatId)
                        R.id.action_clear_chat -> clearChat(chatId)
                        R.id.action_delete_chat -> deleteChat(chatId)
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun markFavourite(userModel: UserModel, favourite: String) {
        viewModel.favouriteChat(userModel, favourite) {
            if (it == null)
                return@favouriteChat
            Toast.makeText(
                this,
                if (!userModel.favourites.contains(favourite))
                    "Marked as Favourite"
                else "Clear Favourite",
                Toast.LENGTH_SHORT
            ).show()
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

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        //init the recycler view
        initMenu(chatId, loggedInUsername)
        initRecyclerView(chatId, loggedInUsername)
        initUserStatus(chatId, loggedInUsername)
        initSendingLayout(chatId)
        initOpenProfile(chatId, loggedInUsername)

        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.getLiveChatDetails(chatId).observe(this) {
            if (it != null) {
                //it requires real time updated

                binding.loadingLottie.visibility = View.GONE

                //setting the profile image
                val chatProfileImage = ChatUtils.getUserChatProfileImage(it, loggedInUsername)
                Glide
                    .with(this)
                    .load(chatProfileImage)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.chattingProfileImage)

                //setting the username
                val headerUsername = ChatUtils.getUserChatUsername(it, loggedInUsername)
                binding.chattingUsername.text = headerUsername

                //submit the live list to the adapter
                chattingAdapter.submitList(it.chatMessages) {
                    //when the list is submitted, then update the seen status
                    viewModel.updateSeen(this, chatId) {}
                    //scroll to the bottom of the recycler view
                    binding.chattingRv.smoothScrollToPosition(it.chatMessages.size - 1)
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

    private fun initRecyclerView(chatId: String, loggedInUsername: String) {
        val chatModel = viewModel.getChatDetails(chatId)
        binding.chattingRv.apply {
            chattingAdapter =
                ChattingRecyclerAdapter(loggedInUsername, chatModel!!, this@ChatActivity)
            adapter = chattingAdapter
        }

        //scroll to the bottom of the recycler view when the keyboard is open acc to live data
        viewModel.getLiveChatDetails(chatId).observe(this) {
            binding.chattingRv.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                //scroll to the bottom of the recycler view on keyboard open
                if (bottom < oldBottom) {
                    binding.chattingRv.smoothScrollToPosition(it!!.chatMessages.size - 1)
                }
            }
        }
    }

    private fun initUserStatus(chatId: String, loggedInUsername: String) {
        viewModel.listenUserStatus(chatId, loggedInUsername) { status ->
            //setting the status
            if (status == null) {
                binding.activityChattingStatusCv.visibility = View.GONE
                return@listenUserStatus
            }

            if (status == UserStatus.Online.name) {
                binding.activityChattingStatusCv.visibility = View.VISIBLE
                binding.chattingStatus.visibility = View.VISIBLE

                binding.activityChattingStatusCv.setCardBackgroundColor(
                    resources.getColor(
                        R.color.green,
                        null
                    )
                )
                binding.chattingStatus.text = UserStatus.Online.name
            } else if (status.split(" ")[0] == UserStatus.LastSeen.name) {
                binding.activityChattingStatusCv.visibility = View.VISIBLE
                binding.chattingStatus.visibility = View.VISIBLE

                binding.activityChattingStatusCv.setCardBackgroundColor(
                    resources.getColor(
                        R.color.yellow,
                        null
                    )
                )
                //getting the last seen time
                val lastSeen = status.split(" ")[1].toLong()
                val lastSeenTime = TimeUtils.getFormattedTime(Timestamp(lastSeen, 0))

                binding.chattingStatus.text =
                    getString(R.string.chatting_activity_text_last_seen, lastSeenTime)
            }
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

        //photo sending button
        binding.photoProgressBar.visibility = View.GONE
        binding.photoAddBtn.visibility = View.VISIBLE

        binding.photoAddBtn.setOnClickListener {
            binding.photoAddBtn.visibility = View.GONE
            binding.photoProgressBar.visibility = View.VISIBLE
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //sticker sending button
        binding.stickerAddBtn.setOnClickListener {
            val stickersBottomSheet = StickersBottomSheet {
                viewModel.sendSticker(this, chatId, it) {}
            }
            stickersBottomSheet.show(supportFragmentManager, stickersBottomSheet.tag)
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
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
                        val uCrop = UCrop.of(pickedPhotoUri, uri!!)
                            .withMaxResultSize(1080, 1080)

                        cropImageCallback.launch(uCrop.getIntent(this))
                    }

            } else {
                binding.photoAddBtn.visibility = View.VISIBLE
                binding.photoProgressBar.visibility = View.GONE
            }
        }

    /**
     * CALLBACK FOR CROPPING RECEIVED IMAGE
     */
    private var cropImageCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = UCrop.getOutput(result.data!!)

                if (selectedImageUri == null) {
                    binding.photoAddBtn.visibility = View.VISIBLE
                    binding.photoProgressBar.visibility = View.GONE
                    Toast.makeText(this, "Error cropping image", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                viewModel.sendImageMessage(this, chatId, selectedImageUri) {
                    if (it == null) {
                        Toast.makeText(this, "Error sending image", Toast.LENGTH_SHORT).show()
                    }
                    binding.photoAddBtn.visibility = View.VISIBLE
                    binding.photoProgressBar.visibility = View.GONE
                }
            } else {
                binding.photoAddBtn.visibility = View.VISIBLE
                binding.photoProgressBar.visibility = View.GONE
            }
        }

    private fun initOpenProfile(chatId: String, loggedInUsername: String) {
        binding.chattingProfileImage.setOnClickListener {
            openProfile(chatId, loggedInUsername)
        }
        binding.chattingUsername.setOnClickListener {
            openProfile(chatId, loggedInUsername)
        }
        binding.activityChattingStatusCv.setOnClickListener {
            openProfile(chatId, loggedInUsername)
        }
    }

    private fun openProfile(chatId: String, loggedInUsername: String) {
        val intent = Intent(this, ChatProfileActivity::class.java)
        intent.putExtra(ChatConstants.CHAT_ID, chatId)
        intent.putExtra(UserConstants.USERNAME, loggedInUsername)
        profileLauncher.launch(intent)
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.DELETE_CHAT) {
                finish()
            }
        }

    private fun sendMessage(message: String, chatId: String) {
        viewModel.sendTextMessage(this, chatId, message) {
            if (it == null) {
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImageClicked(chatMessageModel: ChatMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, chatMessageModel.image)
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                chatImageIv,
                getString(R.string.chatting_activity_chat_image_transition)
            )

        startActivity(intent, activityOptionsCompat.toBundle())
    }
}