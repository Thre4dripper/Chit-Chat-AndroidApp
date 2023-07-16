package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
                    } else {
                        setTextColor(ContextCompat.getColor(context, R.color.darkGrey))
                    }
                }
                binding.userDetailsNameTv.apply {
                    text = it.name
                    if (it.name == "") {
                        text = getString(R.string.user_details_activity_no_name)
                        setTextColor(ContextCompat.getColor(context, R.color.lightGrey))
                    } else {
                        setTextColor(ContextCompat.getColor(context, R.color.darkGrey))
                    }
                }
                binding.userDetailsBioTv.apply {
                    text = it.bio
                    if (it.bio == "") {
                        text = getString(R.string.user_details_activity_no_bio)
                        setTextColor(ContextCompat.getColor(context, R.color.lightGrey))
                    }
                }

                //check if username is set, if not, disable all buttons except username button
                setProfileImageBtn(binding, it.username != "")
                setUsernameBtn(binding)
                setNameBtn(binding, it.username != "")
                setBioBtn(binding, it.username != "")
            }
        }

        viewModel.getUserDetails(this) {
            if (!it) {
                Toast.makeText(this, "Error getting user details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setProfileImageBtn(
        binding: ActivityUserDetailsBinding,
        isUsernameSet: Boolean
    ) {
        binding.userDetailsSetProfileImage.setOnClickListener {
            if (!isUsernameSet) {
                Toast.makeText(this, "Set username first", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageRequest.launch(intent)
        }
    }

    private val imageRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    viewModel.updateProfilePicture(this, imageUri) {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Error getting image", Toast.LENGTH_SHORT)
                        .show()
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

    private fun setNameBtn(binding: ActivityUserDetailsBinding, isUsernameSet: Boolean) {
        binding.userDetailsEditName.setOnClickListener {
            if (!isUsernameSet) {
                Toast.makeText(this, "Set username first", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_NAME)
            startActivity(intent)
        }
    }

    private fun setBioBtn(binding: ActivityUserDetailsBinding, isUsernameSet: Boolean) {
        binding.userDetailsEditBio.setOnClickListener {
            if (!isUsernameSet) {
                Toast.makeText(this, "Set username first", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, SetDetailsActivity::class.java)
            intent.putExtra(Constants.FRAGMENT_TYPE, Constants.FRAGMENT_BIO)
            startActivity(intent)
        }
    }
}