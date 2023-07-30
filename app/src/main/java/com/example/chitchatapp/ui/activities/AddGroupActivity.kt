package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.AddGroupRecyclerAdapter
import com.example.chitchatapp.adapters.GroupSelectedRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.AddGroupInterface
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityAddGroupBinding
import com.example.chitchatapp.databinding.DialogCreateGroupBinding
import com.example.chitchatapp.viewModels.AddGroupViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddGroupActivity : AppCompatActivity(), AddGroupInterface {
    private val TAG = "AddGroupActivity"

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var createGroupDialogBinding: DialogCreateGroupBinding
    private lateinit var viewModel: AddGroupViewModel

    private var selectedGroupImageUri: Uri? = null

    private lateinit var addGroupAdapter: AddGroupRecyclerAdapter
    private lateinit var selectedUsersAdapter: GroupSelectedRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_group)
        viewModel = ViewModelProvider(this)[AddGroupViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)

        binding.addGroupBackBtn.setOnClickListener {
            finish()
        }

        if (loggedInUsername != null) {
            initSelectedUserRecyclerView(loggedInUsername)
            initChatsRecyclerView(loggedInUsername)
            initCreateGrpBtn()
            searchUsers(loggedInUsername)
        } else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initSelectedUserRecyclerView(loggedInUsername: String) {
        binding.addGroupSelectedContactsRv.apply {
            selectedUsersAdapter =
                GroupSelectedRecyclerAdapter(loggedInUsername, this@AddGroupActivity)
            adapter = selectedUsersAdapter
        }

        AddGroupViewModel.selectedUsers.observe(this) {
            selectedUsersAdapter.submitList(it)

            //if no user is selected then hide the save button
            binding.addGroupSaveBtn.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initChatsRecyclerView(loggedInUsername: String) {
        binding.addGroupContactsRv.apply {
            addGroupAdapter = AddGroupRecyclerAdapter(
                loggedInUsername,
                AddGroupViewModel.selectedUsers, //initially selected users will be empty
                this@AddGroupActivity
            )
            adapter = addGroupAdapter
        }

        viewModel.searchedUsers.observe(this) {
            binding.addChatsNoResultsLottie.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            addGroupAdapter.submitList(it)
        }
    }

    private fun initCreateGrpBtn() {
        binding.addGroupSaveBtn.visibility = View.GONE
        binding.addGroupSaveBtn.setOnClickListener {
            openCreateGroupDialog()
        }
    }

    private fun searchUsers(loggedInUsername: String) {
        //initially we will show all the users
        viewModel.searchUsers("", loggedInUsername)
        binding.addChatsNoResultsLottie.visibility = View.GONE

        binding.addGroupSearchEt.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.searchUsers(text.toString(), loggedInUsername)
        })
    }

    override fun onUserClicked(chatId: String) {
        AddGroupViewModel.selectedUsers.value?.find { it.chatId == chatId }?.let {
            viewModel.removeSelectedUser(it)
        } ?: run {
            viewModel.searchedUsers.value?.find { it.chatId == chatId }?.let {
                viewModel.addSelectedUser(it)
            }
        }

        val index = viewModel.searchedUsers.value?.indexOfFirst { it.chatId == chatId }
        addGroupAdapter.notifyItemChanged(index!!)
    }

    private fun openCreateGroupDialog() {
        selectedGroupImageUri = null
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle("Create Group")

        createGroupDialogBinding = DialogCreateGroupBinding.inflate(layoutInflater)
        dialog.setView(createGroupDialogBinding.root)

        createGroupDialogBinding.createGroupCv.setOnClickListener {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialog.setPositiveButton("Create") { _, _ ->
            val groupImageUri = selectedGroupImageUri
            val groupName = createGroupDialogBinding.createGroupEt.text.toString()
            if (groupName.trim().isNotEmpty()) {
                //start group chat activity
                val intent = Intent(this, GroupChatActivity::class.java)
                intent.putExtra(GroupConstants.GROUP_NAME, groupName)
                intent.putExtra(GroupConstants.GROUP_IMAGE, groupImageUri.toString())
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Cancel") { _, _ -> }
        dialog.show()

    }

    // Registers a photo picker activity launcher in single-select mode.
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia())
        { pickedPhotoUri ->
            if (pickedPhotoUri == null) return@registerForActivityResult
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
                    val uCrop = UCrop.of(pickedPhotoUri, uri!!).withAspectRatio(1f, 1f)
                        .withMaxResultSize(1080, 1080)

                    cropImageCallback.launch(uCrop.getIntent(this))
                }
        }

    /**
     * CALLBACK FOR CROPPING RECEIVED IMAGE
     */
    private var cropImageCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedGroupImageUri = UCrop.getOutput(result.data!!)

                if (selectedGroupImageUri == null) {
                    Toast.makeText(this, "Error cropping image", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                Glide.with(this)
                    .load(selectedGroupImageUri)
                    .placeholder(R.drawable.ic_group)
                    .circleCrop()
                    .into(createGroupDialogBinding.createGroupIv)
            } else {
                binding.addGroupSaveBtn.visibility = View.VISIBLE
            }
        }
}