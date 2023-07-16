package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityAddChatsBinding
import com.example.chitchatapp.viewModels.AddChatsViewModel

class AddChatsActivity : AppCompatActivity() {
    private val TAG = "AddChatsActivity"

    private lateinit var binding: ActivityAddChatsBinding
    private lateinit var viewModel: AddChatsViewModel

    private lateinit var debounceHandler: Handler
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
            Toast.makeText(this, "Search result: ${it.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchUsers() {
        debounceHandler = Handler(mainLooper)
        binding.addChatsSearchEt.addTextChangedListener(onTextChanged = { text, _, _, _ ->

            //removing all callbacks and messages from the handler
            debounceHandler.removeCallbacksAndMessages(null)

            //adding a new callback to the handler
            debounceHandler = Handler(mainLooper)

            //adding a delay of 500ms to the callback
            debounceHandler.postDelayed({
                if (text.toString().isNotEmpty()) {
                    viewModel.searchUsers(text.toString())
                }
            }, 500)
        })
    }
}