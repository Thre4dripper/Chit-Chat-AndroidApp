package com.example.chitchatapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.GroupProfileMediaRecyclerAdapter
import com.example.chitchatapp.adapters.interfaces.GroupProfileClickInterface
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.databinding.ActivityGroupProfileBinding
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.models.GroupMessageModel
import com.example.chitchatapp.viewModels.GroupChatViewModel

class GroupProfileActivity : AppCompatActivity(), GroupProfileClickInterface {
    private lateinit var binding: ActivityGroupProfileBinding
    private lateinit var groupChatViewModel: GroupChatViewModel

    private lateinit var mediaAdapter: GroupProfileMediaRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_profile)
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]

        binding.groupProfileBackBtn.setOnClickListener {
            finish()
        }

        val groupId = intent.getStringExtra(GroupConstants.GROUP_ID)
        initMediaRecycleView()
        getGroupDetails(groupId!!)
    }

    private fun getGroupDetails(groupId: String) {
        groupChatViewModel.getLiveGroupChatDetails(groupId).observe(this) {
            if (it == null) return@observe

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_group)
                .circleCrop()
                .into(binding.groupProfileProfileIv)

            binding.groupProfileNameTv.text = it.name

            val chatMediasList = it.messages.filter { chatMessageModel ->
                chatMessageModel.type == GroupMessageType.TypeImage
            }

            if (chatMediasList.isEmpty()) {
                binding.groupProfileMediaRv.visibility = View.GONE
                binding.groupProfileNoMediaLottie.visibility = View.VISIBLE
            } else {
                binding.groupProfileMediaRv.visibility = View.VISIBLE
                binding.groupProfileNoMediaLottie.visibility = View.GONE
            }

            mediaAdapter.submitList(chatMediasList)
        }
    }

    private fun initMediaRecycleView() {
        binding.groupProfileMediaRv.apply {
            mediaAdapter = GroupProfileMediaRecyclerAdapter(this@GroupProfileActivity)
            adapter = mediaAdapter
        }
    }

    override fun onMediaImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView) {
        val intent = Intent(this, ZoomActivity::class.java)
        intent.putExtra(Constants.ZOOM_IMAGE_URL, groupMessageModel.image)
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                chatImageIv,
                getString(R.string.chatting_activity_chat_image_transition)
            )

        startActivity(intent, activityOptionsCompat.toBundle())
    }

    override fun onGroupMemberClicked(username: String) {

    }
}