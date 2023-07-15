package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.Constants
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ActivityUserDetailsBinding
import com.example.chitchatapp.viewModels.UserDetailsViewModel

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var viewModel: UserDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]

        initBackButtons()
        getProfile(binding)
//        setUsername(binding)
//        setName(binding)
//        setBio(binding)
    }

    private fun initBackButtons() {
        binding.userDetailsBackBtn.setOnClickListener {
            finish()
        }
        binding.userDetailsBackBtnTv.setOnClickListener {
            finish()
        }
    }

    private fun getProfile(binding: ActivityUserDetailsBinding) {
        viewModel.userDetails.observe(this) {
            if (it != null) {
                Glide
                    .with(this)
                    .load(it.profileImage)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.userDetailsProfileIv)

                binding.userDetailsUsernameTv.text = it.username
                binding.userDetailsNameTv.text = it.name
                binding.userDetailsBioTv.text = it.bio
            }
        }

        viewModel.getUserDetails(this) {
            if (!it) {
                Toast.makeText(this, "Error getting user details", Toast.LENGTH_SHORT).show()
            }
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