package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemChatReceiverTextBinding
import com.example.chitchatapp.databinding.ItemChatSenderTextBinding
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

    companion object {
        const val VIEW_TYPE_FIRST_MESSAGE = 0
        const val VIEW_TYPE_SENDER_MESSAGE = 1
        const val VIEW_TYPE_RECEIVER_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_hello_message, parent, false)
                HelloMessageViewHolder(view)
            }

            VIEW_TYPE_RECEIVER_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_receiver_text, parent, false)
                ReceiverTextViewHolder(view)
            }

            VIEW_TYPE_SENDER_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sender_text, parent, false)
                SenderTextViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_FIRST_MESSAGE -> {}
            VIEW_TYPE_SENDER_MESSAGE -> {
                val viewHolder = holder as SenderTextViewHolder
                viewHolder.bind(item)
            }

            VIEW_TYPE_RECEIVER_MESSAGE -> {
                val viewHolder = holder as ReceiverTextViewHolder
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
                return if (item.chatMessageTo == loggedInUsername) {
                    VIEW_TYPE_RECEIVER_MESSAGE
                } else {
                    VIEW_TYPE_SENDER_MESSAGE
                }
            }

            else -> -1
        }
    }

    inner class HelloMessageViewHolder(itemView: View) : ViewHolder(itemView)
    inner class ReceiverTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatReceiverTextBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            binding.itemChatReceiverTextMessage.text = chatMessageModel.chatMessage
            binding.itemChatReceiverTextTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.chatMessageTime)
        }
    }

    inner class SenderTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatSenderTextBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatSenderIv)

            binding.itemChatSenderTextMessage.text = chatMessageModel.chatMessage
            binding.itemChatSenderTextTime.text =
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