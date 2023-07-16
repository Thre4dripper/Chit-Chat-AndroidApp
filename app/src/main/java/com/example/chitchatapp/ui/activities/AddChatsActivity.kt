package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityAddChatsBinding

class AddChatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddChatsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_chats)

        binding.addChatsBackBtn.setOnClickListener {
           finish()
        }
        binding.addChatsSearchEt.requestFocus()
    }
}