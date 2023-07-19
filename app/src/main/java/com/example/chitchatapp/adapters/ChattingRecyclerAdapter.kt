package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemChatLeftTextBinding
import com.example.chitchatapp.databinding.ItemChatRightTextBinding
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel

class ChattingRecyclerAdapter(
    private var loggedInUsername: String,
    private var chatModel: ChatModel
) :
    ListAdapter<ChatMessageModel, ViewHolder>(ChatMessageDiffCallback()) {
    private val TAG = "ChattingRecyclerAdapter"

    companion object {
        const val VIEW_TYPE_FIRST_MESSAGE = 0
        const val VIEW_TYPE_LEFT_MESSAGE = 1
        const val VIEW_TYPE_RIGHT_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_hello_message, parent, false)
                HelloMessageViewHolder(view)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_right_text, parent, false)
                RightTextViewHolder(view)
            }

            VIEW_TYPE_LEFT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_left_text, parent, false)
                LeftTextViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_FIRST_MESSAGE -> {}
            VIEW_TYPE_LEFT_MESSAGE -> {
                val viewHolder = holder as LeftTextViewHolder
                viewHolder.bind(item)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val viewHolder = holder as RightTextViewHolder
                viewHolder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.chatMessageType) {
            ChatMessageType.TypeFirstMessage -> {
                return VIEW_TYPE_FIRST_MESSAGE
            }

            ChatMessageType.TypeMessage -> {
                return if (item.chatMessageFrom == loggedInUsername) {
                    VIEW_TYPE_RIGHT_MESSAGE
                } else {
                    VIEW_TYPE_LEFT_MESSAGE
                }
            }

            else -> -1
        }
    }

    inner class HelloMessageViewHolder(itemView: View) : ViewHolder(itemView)
    inner class RightTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatRightTextBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            binding.itemChatRightTextMessage.text = chatMessageModel.chatMessage
            binding.itemChatRightTextTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.chatMessageTime)

            val senderImage = ChatUtils.getChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatMessageStatusIv)

            val senderUsername = ChatUtils.getChatUsername(chatModel, loggedInUsername)
            binding.itemChatMessageStatusIv.visibility =
                if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    inner class LeftTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatLeftTextBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatLeftIv)

            binding.itemChatLeftTextMessage.text = chatMessageModel.chatMessage
            binding.itemChatLeftTextTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.chatMessageTime)
        }
    }

    class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessageModel>() {
        override fun areItemsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem.chatMessageId == newItem.chatMessageId
        }

        override fun areContentsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}