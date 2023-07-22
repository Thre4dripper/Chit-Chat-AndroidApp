package com.example.chitchatapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityAddGroupBinding
import com.example.chitchatapp.viewModels.AddGroupViewModel

class AddGroupActivity : AppCompatActivity() {
    private val TAG = "AddGroupActivity"

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var viewModel: AddGroupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_group)
        viewModel = ViewModelProvider(this)[AddGroupViewModel::class.java]

        val loggedInUsername = intent.getStringExtra(UserConstants.USERNAME)

        binding.addGroupBackBtn.setOnClickListener {
            finish()
        }

        if (loggedInUsername != null)
            searchUsers(loggedInUsername)
        else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun searchUsers(loggedInUsername: String) {
        viewModel.searchUsers("", loggedInUsername)
        binding.addChatsNoResultsLottie.visibility = View.GONE

        binding.addGroupSearchEt.addTextChangedListener(onTextChanged = { text, _, _, _ ->
//            addChatsAdapter.submitList(listOf())
            viewModel.searchUsers(text.toString(), loggedInUsername).observe(this) {
                Log.d(TAG, "searchUsers: $it")
            }
        })
    }
}