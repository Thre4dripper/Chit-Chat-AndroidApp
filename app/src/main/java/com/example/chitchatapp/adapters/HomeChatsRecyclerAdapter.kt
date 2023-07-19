package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatClickInterface
import com.example.chitchatapp.databinding.ItemHomeChatBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatModel

class HomeChatsRecyclerAdapter(
    private var loggedInUsername: String,
    private var chatClickInterface: ChatClickInterface
) :
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

            val profileImage = ChatUtils.getChatProfileImage(
                chatModel,
                loggedInUsername
            )

            Glide.with(context)
                .load(profileImage)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.itemHomeChatProfileImage)

            binding.apply {
                val username = ChatUtils.getChatUsername(
                    chatModel,
                    loggedInUsername
                )
                itemHomeChatUsername.text = username
                itemHomeChatMessageTime.text =
                    TimeUtils.getFormattedTime(chatModel.chatMessages.last().chatMessageTime)

                itemHomeChatMessage.text = chatModel.chatMessages.last().chatMessage?.trim() ?: ""

                val unreadMessages = chatModel.chatMessages.filter {
                    !it.chatMessageSeen.contains(loggedInUsername)
                }.size
                itemHomeChatCountTv.text = unreadMessages.toString()
                itemHomeChatCountCv.visibility = if (unreadMessages > 0) View.VISIBLE else View.GONE

            }

            binding.root.setOnClickListener {
                chatClickInterface.onChatClicked(chatModel.chatId)
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