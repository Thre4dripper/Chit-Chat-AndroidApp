package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityAddChatsBinding
import com.example.chitchatapp.viewModels.AddChatsViewModel

class AddChatsActivity : AppCompatActivity() {
    private val TAG = "AddChatsActivity"

    private lateinit var binding: ActivityAddChatsBinding
    private lateinit var viewModel: AddChatsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chats)
        viewModel = ViewModelProvider(this)[AddChatsViewModel::class.java]

        binding.addChatsBackBtn.setOnClickListener {
            finish()
        }
        binding.addChatsSearchEt.requestFocus()

        searchListener()
        searchUsers()
    }

    private fun searchListener() {
        viewModel.searchedUsers.observe(this) {
            Log.d(TAG, "searchListener: $it")
        }
    }

    private fun searchUsers() {
        binding.addChatsSearchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchUsers(binding.addChatsSearchEt.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}