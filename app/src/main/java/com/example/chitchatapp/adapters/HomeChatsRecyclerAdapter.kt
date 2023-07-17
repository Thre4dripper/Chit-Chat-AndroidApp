package com.example.chitchatapp.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemHomeChatBinding
import com.example.chitchatapp.models.ChatModel

class HomeChatsRecyclerAdapter(private var loggedInUsername: String) :
    ListAdapter<ChatModel, HomeChatsRecyclerAdapter.ChatsViewHolder>(ChatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_chat, parent, false)

        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chatModel = getItem(position)
        holder.bind(chatModel)
    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHomeChatBinding.bind(itemView)

        fun bind(chatModel: ChatModel) {
            val context = itemView.context

            val profileImage = if (chatModel.chatUsername1 == loggedInUsername) {
                chatModel.chatProfileImage2
            } else {
                chatModel.chatProfileImage1
            }

            Glide.with(context)
                .load(profileImage)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.itemHomeChatProfileImage)

            binding.apply {
                loggedInUsername = if (chatModel.chatUsername1 == loggedInUsername) {
                    chatModel.chatUsername2
                } else {
                    chatModel.chatUsername1
                }
                itemHomeChatUsername.text = loggedInUsername

                val date = chatModel.chatMessages.last().chatMessageTime.toDate()
                val time = DateUtils.getRelativeTimeSpanString(
                    date.time,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
                itemHomeChatMessageTime.text = time

                itemHomeChatMessage.text = chatModel.chatMessages.last().chatMessage?.trim() ?: ""
            }
        }
    }

    class ChatsDiffCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
}