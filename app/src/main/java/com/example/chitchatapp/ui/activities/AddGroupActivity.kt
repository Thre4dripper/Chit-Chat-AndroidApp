package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.AddGroupRecyclerAdapter
import com.example.chitchatapp.adapters.GroupSelectedRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.ChatClickInterface
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityAddGroupBinding
import com.example.chitchatapp.viewModels.AddGroupViewModel

class AddGroupActivity : AppCompatActivity(), ChatClickInterface {
    private val TAG = "AddGroupActivity"

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var viewModel: AddGroupViewModel

    private lateinit var addGroupAdapter: AddGroupRecyclerAdapter
    private lateinit var selectedUsersAdapter: GroupSelectedRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_group)
        viewModel = ViewModelProvider(this)[AddGroupViewModel::class.java]

        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)

        binding.addGroupBackBtn.setOnClickListener {
            finish()
        }

        if (loggedInUsername != null) {
            initSelectedUserRecyclerView(loggedInUsername)
            initChatsRecyclerView(loggedInUsername)
            searchUsers(loggedInUsername)
        } else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initSelectedUserRecyclerView(loggedInUsername: String) {
        binding.addGroupSelectedContactsRv.apply {
            selectedUsersAdapter = GroupSelectedRecyclerAdapter(loggedInUsername)
            adapter = selectedUsersAdapter
        }

        viewModel.selectedUsers.observe(this) {
            selectedUsersAdapter.submitList(it)
        }
    }

    private fun initChatsRecyclerView(loggedInUsername: String) {
        binding.addGroupContactsRv.apply {
            addGroupAdapter = AddGroupRecyclerAdapter(
                loggedInUsername,
                viewModel.selectedUsers.value!!, //initially selected users will be empty
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

    private fun searchUsers(loggedInUsername: String) {
        //initially we will show all the users
        viewModel.searchUsers("", loggedInUsername)
        binding.addChatsNoResultsLottie.visibility = View.GONE

        binding.addGroupSearchEt.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            viewModel.searchUsers(text.toString(), loggedInUsername)
        })
    }

    override fun onChatClicked(chatId: String) {
        viewModel.selectedUsers.value?.find { it.chatId == chatId }?.let {
            viewModel.removeSelectedUser(it)
        } ?: run {
            viewModel.searchedUsers.value?.find { it.chatId == chatId }?.let {
                viewModel.addSelectedUser(it)
            }
        }

        val index = viewModel.searchedUsers.value?.indexOfFirst { it.chatId == chatId }
        addGroupAdapter.notifyItemChanged(index!!)
    }
}