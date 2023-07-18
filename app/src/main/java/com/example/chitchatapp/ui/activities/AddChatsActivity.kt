package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.AddChatsRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.AddChatInterface
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.databinding.ActivityAddChatsBinding
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.viewModels.AddChatsViewModel

class AddChatsActivity : AppCompatActivity(), AddChatInterface {
    private val TAG = "AddChatsActivity"

    private lateinit var binding: ActivityAddChatsBinding
    private lateinit var viewModel: AddChatsViewModel

    private lateinit var addChatsAdapter: AddChatsRecyclerAdapter

    private lateinit var debounceHandler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chats)
        viewModel = ViewModelProvider(this)[AddChatsViewModel::class.java]

        binding.addChatsBackBtn.setOnClickListener {
            finish()
        }
        binding.addChatsSearchEt.requestFocus()

        initRecyclerView()
        searchUsers()
    }

    private fun initRecyclerView() {
        addChatsAdapter = AddChatsRecyclerAdapter(this)
        binding.addChatsRv.apply {
            adapter = addChatsAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@AddChatsActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(this@AddChatsActivity)
        }

        viewModel.searchedUsers.observe(this) {
            binding.addChatsNoResultsLottie.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
            addChatsAdapter.submitList(it)

            //hiding loading lottie
            binding.addChatsLoadingLottie.visibility = View.GONE
        }
    }

    private fun searchUsers() {
        //initial work
        debounceHandler = Handler(mainLooper)
        viewModel.searchUsers("")
        binding.addChatsNoResultsLottie.visibility = View.GONE
        binding.addChatsLoadingLottie.visibility = View.VISIBLE


        binding.addChatsSearchEt.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            //removing all callbacks and messages from the handler
            debounceHandler.removeCallbacksAndMessages(null)

            //adding a new callback to the handler
            debounceHandler = Handler(mainLooper)

            //adding a delay of 500ms to the callback
            debounceHandler.postDelayed({
                binding.addChatsLoadingLottie.visibility = View.VISIBLE
                binding.addChatsNoResultsLottie.visibility = View.GONE
                addChatsAdapter.submitList(listOf())

                viewModel.searchUsers(text.toString())
            }, 500)
        })
    }

    override fun onAddChat(userModel: UserModel) {
        //start chat activity
        val intent = Intent(this, ChattingActivity::class.java)
        intent.putExtra(UserConstants.USER_MODEL, userModel)
        startActivity(intent)
        finish()
        viewModel.addChat(userModel) {}
    }
}