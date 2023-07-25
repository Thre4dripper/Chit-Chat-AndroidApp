package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.AddGroupInterface
import com.example.chitchatapp.databinding.ItemGroupSelectedContactBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel

class GroupSelectedRecyclerAdapter(
    private var loggedInUsername: String,
    private var addGroupInterface: AddGroupInterface
) :
    ListAdapter<ChatModel, GroupSelectedRecyclerAdapter.SelectedUserViewHolder>(
        SelectedUserDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group_selected_contact, parent, false)

        return SelectedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedUserViewHolder, position: Int) {
        val selectedResult = getItem(position)
        holder.bind(selectedResult)
    }

    inner class SelectedUserViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemGroupSelectedContactBinding.bind(itemView)

        fun bind(chatModel: ChatModel) {
            val context = itemView.context

            val profileImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(context)
                .load(profileImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemGroupSelectedIv)

            //zoom in and out animation
            val animation = android.view.animation.AnimationUtils.loadAnimation(
                context,
                com.bumptech.glide.R.anim.abc_slide_in_bottom
            )
            animation.duration = 300
            binding.itemGroupSelectedIv.startAnimation(animation)

            binding.itemGroupSelectedCancelBtn.setOnClickListener {
                addGroupInterface.onUserClicked(chatModel.chatId)
            }
        }
    }

    class SelectedUserDiffCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
}