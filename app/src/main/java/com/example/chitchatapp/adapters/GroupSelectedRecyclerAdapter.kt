package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatClickInterface
import com.example.chitchatapp.databinding.ItemGroupSelectedContactBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel

class GroupSelectedRecyclerAdapter(
    private var loggedInUsername: String,
    private var ChatClickInterface: ChatClickInterface
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

            val profileImage = ChatUtils.getChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(context)
                .load(profileImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemGroupSelectedIv)

            binding.itemGroupSelectedCancelBtn.setOnClickListener {
                ChatClickInterface.onChatClicked(chatModel.chatId)
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