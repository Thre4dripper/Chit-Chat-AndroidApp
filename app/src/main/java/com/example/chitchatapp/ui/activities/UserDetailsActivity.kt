package com.example.chitchatapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chitchatapp.Constants
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityUserDetailsBinding
import com.example.chitchatapp.firebase.firestore.FirestoreUtils
import com.google.firebase.auth.FirebaseAuth

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)

        initBackButtons()
        getProfileImage(binding)
        setUsername(binding)
        setName(binding)
        setBio(binding)
    }

    private fun initBackButtons() {
        binding.userDetailsBackBtn.setOnClickListener {
            finish()
        }
        binding.userDetailsBackBtnTv.setOnClickListener {
            finish()
        }
    }

    private fun getProfileImage(binding: ActivityUserDetailsBinding) {
        FirestoreUtils.getUserProfileImage(auth.currentUser!!) {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.userDetailsProfileIv)
        }
    }

    private fun setUsername(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditUsername.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_USERNAME)
            startActivity(intent)
        }
    }

    private fun setName(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditName.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_NAME)
            startActivity(intent)
        }
    }

    private fun setBio(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditBio.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_BIO)
            startActivity(intent)
        }
    }
}