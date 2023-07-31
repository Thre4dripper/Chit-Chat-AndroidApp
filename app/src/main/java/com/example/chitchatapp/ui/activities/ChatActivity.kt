package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.ChattingRecyclerAdapter
import com.example.chitchatapp.adapters.SeenByRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.ChatMessageClickInterface
import com.example.chitchatapp.adapters.interfaces.SeenByClickInterface
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityChattingBinding
import com.example.chitchatapp.databinding.SeenByPopupWindowBinding
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.HomeRepository
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

class ChatActivity : AppCompatActivity(), ChatMessageClickInterface, SeenByClickInterface {
    private val TAG = "ChatActivity"

    private lateinit var binding: ActivityChattingBinding
    private lateinit var viewModel: ChatViewModel

    private lateinit var chattingAdapter: ChattingRecyclerAdapter
    private var chatId: String? = null
    private var scrollToBottom = true
    private var prevActivity = false

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //getting intent data
        chatId = intent.getStringExtra(ChatConstants.CHAT_ID)
        val userModel = intent.getSerializableExtra(UserConstants.USER_MODEL) as? UserModel
        prevActivity = intent.getBooleanExtra(Constants.PREV_ACTIVITY, false)

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
        if (chatId != null) {
            getChatDetails(chatId!!, loggedInUsername!!)
        } else {
            createNewChat(userModel!!, loggedInUsername!!)
        }

        binding.chattingBackBtn.setOnClickListener { onBackPressed() }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!prevActivity) {
            //home chats are fetched in background
            //so we need to clear them if this is first activity in the stack (from notifications)
            //so that they are fetched again when we open the home activity, with no errors
            HomeRepository.homeChats.value = null
            startActivity(Intent(this, HomeActivity::class.java))
        }
        supportFinishAfterTransition()
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
                        R.id.action_view_contact -> openProfile(
                            chatId, loggedInUsername, binding.chattingProfileImage
                        )

                        R.id.action_favorite -> markFavourite(userModel!!, chatId)
                        R.id.action_clear_chat -> clearChat()
                        R.id.action_delete_chat -> deleteChat()
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun markFavourite(userModel: UserModel, favourite: String) {
        viewModel.favouriteChat(userModel, favourite) {
            if (it == null) return@favouriteChat
            Toast.makeText(
                this, if (!userModel.favourites.contains(favourite)) "Marked as Favourite"
                else "Clear Favourite", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearChat() {
        clearOrDeleteDialog(
            "Clear chat", "Are you sure you want to clear this chat?"
        ) {
            viewModel.clearChat {
                Toast.makeText(
                    this, if (it) "Chat cleared" else "Error clearing chat", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteChat() {
        clearOrDeleteDialog(
            "Delete chat", "Are you sure you want to delete this chat?"
        ) {
            viewModel.deletedChat {
                Toast.makeText(
                    this, if (it) "Chat deleted" else "Error deleting chat", Toast.LENGTH_SHORT
                ).show()
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

    private fun getChatDetails(chatId: String, loggedInUsername: String) {
        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.chatDetails.observe(this) {
            if (it == null) return@observe
            binding.loadingLottie.visibility = View.GONE

            //init everything after getting the chat details
            initMenu(chatId, loggedInUsername)
            initRecyclerView(loggedInUsername)
            initUserStatus(loggedInUsername)
            initSendingLayout()
            initOpenProfile(chatId, loggedInUsername)

            //setting the profile image
            val chatProfileImage = ChatUtils.getUserChatProfileImage(it, loggedInUsername)
            Glide.with(this).load(chatProfileImage).placeholder(R.drawable.ic_profile).circleCrop()
                .into(binding.chattingProfileImage)

            //setting the username
            val headerUsername = ChatUtils.getUserChatUsername(it, loggedInUsername)
            binding.chattingUsername.text = headerUsername

            //submit the live list to the adapter
            chattingAdapter.submitList(it.chatMessages) {
                //when the list is submitted, then update the seen status
                viewModel.updateSeen(this) {}
                //scroll to the bottom of the recycler view
                if (scrollToBottom) binding.chattingRv.smoothScrollToPosition(0)
            }

        }

        viewModel.getChatDetails(chatId) {
            if (it != null) {
                viewModel.updateSeen(this) {}
                viewModel.getLiveChatDetails(chatId)
            }
        }
    }

    private fun createNewChat(userModel: UserModel, loggedInUsername: String) {
        binding.loadingLottie.visibility = View.VISIBLE

        //set the header with available details so far
        binding.chattingUsername.text = userModel.username
        Glide.with(this).load(userModel.profileImage).placeholder(R.drawable.ic_profile)
            .circleCrop().into(binding.chattingProfileImage)

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

    private fun initRecyclerView(loggedInUsername: String) {
        //since this func called everytime list is submitted, so we need to check if it is already initialized
        if (this::chattingAdapter.isInitialized) return
        val chatModel = viewModel.chatDetails.value
        binding.chattingRv.apply {
            chattingAdapter =
                ChattingRecyclerAdapter(loggedInUsername, chatModel!!, this@ChatActivity)
            adapter = chattingAdapter

            //this is for when user is at the bottom of the recycler view
            //then only scroll to bottom for new messages
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //detect end of recycler view
                    //since recycler view is reversed, so we need to check if the first item is visible
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstItemPos = layoutManager.findFirstCompletelyVisibleItemPosition()

                    //if the last visible item is the last item in the list, then scroll to bottom
                    scrollToBottom = firstItemPos == 0

                    binding.chatScrollDownFab.visibility =
                        if (scrollToBottom) View.GONE else View.VISIBLE
                }
            })
        }

        //scroll to bottom when the user clicks on the scroll down fab
        binding.chatScrollDownFab.setOnClickListener {
            binding.chattingRv.smoothScrollToPosition(0)
        }
    }

    private fun initUserStatus(loggedInUsername: String) {
        viewModel.listenUserStatus(loggedInUsername) { status ->
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
                        R.color.green, null
                    )
                )
                binding.chattingStatus.text = UserStatus.Online.name
            } else if (status.split(" ")[0] == UserStatus.LastSeen.name) {
                binding.activityChattingStatusCv.visibility = View.VISIBLE
                binding.chattingStatus.visibility = View.VISIBLE

                binding.activityChattingStatusCv.setCardBackgroundColor(
                    resources.getColor(
                        R.color.yellow, null
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

    private fun initSendingLayout() {
        binding.sendMessageBtn.alpha = 0.5f
        binding.sendMessageBtn.isEnabled = false
        binding.sendMessageEt.addTextChangedListener { text ->
            if (text?.trim().isNullOrEmpty()) {
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
                sendMessage(message)
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
                viewModel.sendSticker(this, it) {}
            }
            stickersBottomSheet.show(supportFragmentManager, stickersBottomSheet.tag)
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { pickedPhotoUri ->
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
                        val uCrop = UCrop.of(pickedPhotoUri, uri!!).withMaxResultSize(1080, 1080)

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

                viewModel.sendImageMessage(this, selectedImageUri) {
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
            openProfile(chatId, loggedInUsername, binding.chattingProfileImage)
        }
        binding.chattingUsername.setOnClickListener {
            openProfile(chatId, loggedInUsername, binding.chattingProfileImage)
        }
        binding.activityChattingStatusCv.setOnClickListener {
            openProfile(chatId, loggedInUsername, binding.chattingProfileImage)
        }
    }

    private fun openProfile(
        chatId: String, loggedInUsername: String, animationView: ImageView
    ) {
        val intent = Intent(this, ChatProfileActivity::class.java)
        intent.putExtra(ChatConstants.CHAT_ID, chatId)
        intent.putExtra(UserConstants.USERNAME, loggedInUsername)
        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, animationView, getString(R.string.chat_profile_activity_profile_image_transition)
        )
        profileLauncher.launch(intent, activityOptionsCompat)
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.DELETE_CHAT) {
                finish()
            }
        }

    private fun sendMessage(message: String) {
        viewModel.sendTextMessage(this, message) {
            //scrolling to bottom when message is sent
            binding.chattingRv.scrollToPosition(0)
            if (it == null) {
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImageClicked(chatMessageModel: ChatMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, chatMessageModel.image)
        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, chatImageIv, getString(R.string.chatting_activity_chat_image_transition)
        )

        startActivity(intent, activityOptionsCompat.toBundle())
    }

    override fun onUserImageClicked(chatImageIv: ImageView) {
        val loggedInUsername = viewModel.getLoggedInUsername(this)
        openProfile(chatId!!, loggedInUsername!!, chatImageIv)
    }

    override fun onSeenByClicked(chatMessageModel: ChatMessageModel, anchor: View) {
        val popupWindow = PopupWindow(this)
        val popupBinding = SeenByPopupWindowBinding.inflate(layoutInflater)
        popupWindow.contentView = popupBinding.root
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popupBinding.btnClose.setOnClickListener {
            popupWindow.dismiss()
        }

        val chatModel = viewModel.chatDetails.value!!

        val seenByAdapter = SeenByRecyclerAdapter(this@ChatActivity)
        popupBinding.seenByRv.apply {
            adapter = seenByAdapter
        }

        val loggedInUsername = viewModel.getLoggedInUsername(this)

        val chatUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername!!)
        val chatProfileImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)

        val list = if (chatMessageModel.seenBy.contains(chatUsername)) listOf(
            GroupChatUserModel(
                chatUsername, chatProfileImage
            )
        )
        else emptyList()
        seenByAdapter.submitList(list)

        popupBinding.seenByNoOne.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        popupWindow.showAsDropDown(anchor, -750, -350, Gravity.END)
    }

    override fun onSeenByClicked(clickedIv: ImageView) {
        val loggedInUsername = viewModel.getLoggedInUsername(this)
        openProfile(chatId!!, loggedInUsername!!, clickedIv)
    }
}