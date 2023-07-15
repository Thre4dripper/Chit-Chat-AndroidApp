package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        setUsernameBtn(binding)
        setNameBtn(binding)
        setBioBtn(binding)
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

                binding.userDetailsUsernameTv.apply {
                    text = it.username
                    if (it.username == "") {
                        text = getString(R.string.user_details_activity_no_username)
                        setTextColor(ContextCompat.getColor(context, R.color.red))
                    }
                }
                binding.userDetailsNameTv.apply {
                    text = it.name
                    if (it.name == "") {
                        text = getString(R.string.user_details_activity_no_name)
                        setTextColor(ContextCompat.getColor(context, R.color.lightGrey))
                    }
                }
                binding.userDetailsBioTv.apply {
                    text = it.bio
                    if (it.bio == "") {
                        text = getString(R.string.user_details_activity_no_bio)
                        setTextColor(ContextCompat.getColor(context, R.color.lightGrey))
                    }
                }
            }
        }

        viewModel.getUserDetails(this) {
            if (!it) {
                Toast.makeText(this, "Error getting user details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUsernameBtn(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditUsername.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_USERNAME)
            startActivity(intent)
        }
    }

    private fun setNameBtn(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditName.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_NAME)
            startActivity(intent)
        }
    }

    private fun setBioBtn(binding: ActivityUserDetailsBinding) {
        binding.userDetailsEditBio.setOnClickListener {
            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_BIO)
            startActivity(intent)
        }
    }
}