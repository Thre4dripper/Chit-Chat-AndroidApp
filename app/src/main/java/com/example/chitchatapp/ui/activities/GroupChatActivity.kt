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
import com.example.chitchatapp.adapters.GroupChatRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.GroupMessageClickInterface
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityGroupChatBinding
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupMessageModel
import com.example.chitchatapp.ui.bottomSheet.StickersBottomSheet
import com.example.chitchatapp.viewModels.AddGroupViewModel
import com.example.chitchatapp.viewModels.GroupChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GroupChatActivity : AppCompatActivity(), GroupMessageClickInterface {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var viewModel: GroupChatViewModel

    private lateinit var groupChatAdapter: GroupChatRecyclerAdapter
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)
        viewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]

        //getting intent data
        groupId = intent.getStringExtra(GroupConstants.GROUP_ID)
        val groupName = intent.getStringExtra(GroupConstants.GROUP_NAME) ?: "Group"
        val groupImage = intent.getStringExtra(GroupConstants.GROUP_IMAGE) ?: ""
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
        if (groupId != null)
            getGroupDetails(groupId!!, loggedInUsername!!)
        else {
            createNewGroup(
                groupName,
                groupImage,
                groupMembers!!,
                loggedInUsername!!
            )
        }

        binding.groupChatBackBtn.setOnClickListener { finish() }
    }

    private fun initMenu(groupId: String, loggedInUsername: String) {
        binding.groupChatMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.group_chat_screen_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_view_details -> openProfile(groupId, loggedInUsername)


                    R.id.action_exit_group -> {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Exit group")
                            .setMessage("Are you sure you want to exit this group?")
                            .setPositiveButton("Yes") { _, _ ->
                                viewModel.exitGroup(this) { isExited ->
                                    Toast.makeText(
                                        this,
                                        if (isExited) "Exited group" else "Error exiting group",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            }
                            .setNegativeButton("No") { _, _ -> }
                            .show()
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun getGroupDetails(groupId: String, loggedInUsername: String) {

        binding.loadingLottie.visibility = View.VISIBLE
        viewModel.groupChatDetails.observe(this) {
            if (it == null) return@observe
            binding.loadingLottie.visibility = View.GONE

            //init everything after getting the group details
            initMenu(groupId, loggedInUsername)
            initRecyclerView(loggedInUsername)
            initMembersLayout()
            initSendingLayout()
            initOpenProfile(groupId, loggedInUsername)

            //setting the group image
            Glide
                .with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_group)
                .circleCrop()
                .into(binding.groupChatGroupImage)

            //setting the group name
            binding.groupChatGroupName.text = it.name

            //submit the live list to the adapter
            groupChatAdapter.submitList(it.messages) {
                //when the list is submitted, then update the seen status
                viewModel.updateSeen(this) {}
                //scroll to the bottom of the recycler view
                binding.groupChatRv.smoothScrollToPosition(0)
            }

        }

        viewModel.getLiveGroupChatDetails(groupId)
    }

    private fun createNewGroup(
        groupName: String,
        groupImage: String,
        groupMembers: List<ChatModel>,
        loggedInUsername: String
    ) {
        binding.loadingLottie.visibility = View.VISIBLE

        //set the header with available details so far
        binding.groupChatGroupName.text = groupName
        Glide
            .with(this)
            .load(groupImage)
            .placeholder(R.drawable.ic_group)
            .circleCrop()
            .into(binding.groupChatGroupImage)

        val groupImageUri = if (groupImage.isEmpty()) null else Uri.parse(groupImage)
        viewModel.createGroup(this, groupName, groupImageUri, groupMembers) {
            binding.loadingLottie.visibility = View.GONE

            //this can only be null when there is an error adding chat
            if (it == null) {
                Toast.makeText(this, "Error creating group", Toast.LENGTH_SHORT).show()
                finish()
            }
            //otherwise if chat already exists then it will be that chat id
            getGroupDetails(it!!, loggedInUsername)
        }
    }

    private fun initRecyclerView(loggedInUsername: String) {
        //since this func called everytime list is submitted, so we need to check if it is already initialized
        if (this::groupChatAdapter.isInitialized) return

        val groupChatModel = viewModel.groupChatDetails.value
        binding.groupChatRv.apply {
            groupChatAdapter =
                GroupChatRecyclerAdapter(loggedInUsername, groupChatModel!!, this@GroupChatActivity)
            adapter = groupChatAdapter
        }
    }

    private fun initMembersLayout() {
        val groupMembers = viewModel.groupChatDetails.value!!.members
        var membersNames = ""
        for (member in groupMembers) {
            membersNames += member.username + ", "
        }

        //removing the last comma
        membersNames = membersNames.substring(0, membersNames.length - 2)
        binding.groupChatMembers.text = membersNames
    }

    private fun initSendingLayout() {
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

    private fun initOpenProfile(groupId: String, loggedInUsername: String) {
        binding.groupChatGroupImage.setOnClickListener {
            openProfile(groupId, loggedInUsername)
        }
        binding.groupChatGroupName.setOnClickListener {
            openProfile(groupId, loggedInUsername)
        }
        binding.groupChatMembers.setOnClickListener {
            openProfile(groupId, loggedInUsername)
        }
    }

    private fun openProfile(groupId: String, loggedInUsername: String) {
        val intent = Intent(this, GroupProfileActivity::class.java)
        intent.putExtra(GroupConstants.GROUP_ID, groupId)
        intent.putExtra(UserConstants.USERNAME, loggedInUsername)
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.groupChatGroupImage,
                getString(R.string.group_profile_activity_profile_image_transition)
            )
        profileLauncher.launch(intent, activityOptionsCompat)
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.EXIT_GROUP) {
                finish()
            }
        }

    private fun sendMessage(message: String) {
        viewModel.sendTextMessage(this, message) {
            if (it == null) {
                Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView) {
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
}