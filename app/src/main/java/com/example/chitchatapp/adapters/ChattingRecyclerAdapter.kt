package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatMessageClickInterface
import com.example.chitchatapp.databinding.ItemChatImageLeftBinding
import com.example.chitchatapp.databinding.ItemChatImageRightBinding
import com.example.chitchatapp.databinding.ItemChatTextLeftBinding
import com.example.chitchatapp.databinding.ItemChatTextRightBinding
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatMessageModel
import com.example.chitchatapp.models.ChatModel

class ChattingRecyclerAdapter(
    private var loggedInUsername: String,
    private var chatModel: ChatModel,
    private var chatMessageClickInterface: ChatMessageClickInterface
) :
    ListAdapter<ChatMessageModel, ViewHolder>(ChatMessageDiffCallback()) {
    private val TAG = "ChattingRecyclerAdapter"

    companion object {
        const val VIEW_TYPE_FIRST_MESSAGE = 0
        const val VIEW_TYPE_LEFT_MESSAGE = 1
        const val VIEW_TYPE_RIGHT_MESSAGE = 2
        const val VIEW_TYPE_LEFT_IMAGE = 3
        const val VIEW_TYPE_RIGHT_IMAGE = 4
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
                    .inflate(R.layout.item_chat_text_right, parent, false)
                RightTextViewHolder(view)
            }

            VIEW_TYPE_LEFT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_left, parent, false)
                LeftTextViewHolder(view)
            }

            VIEW_TYPE_RIGHT_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_right, parent, false)
                RightImageViewHolder(view)
            }

            VIEW_TYPE_LEFT_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_left, parent, false)
                LeftImageViewHolder(view)
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

            VIEW_TYPE_LEFT_IMAGE -> {
                val viewHolder = holder as LeftImageViewHolder
                viewHolder.bind(item)
            }

            VIEW_TYPE_RIGHT_IMAGE -> {
                val viewHolder = holder as RightImageViewHolder
                viewHolder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            ChatMessageType.TypeFirstMessage -> {
                return VIEW_TYPE_FIRST_MESSAGE
            }

            ChatMessageType.TypeMessage -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_MESSAGE
                } else {
                    VIEW_TYPE_LEFT_MESSAGE
                }
            }

            ChatMessageType.TypeImage -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_IMAGE
                } else {
                    VIEW_TYPE_LEFT_IMAGE
                }
            }

            else -> -1
        }
    }

    inner class HelloMessageViewHolder(itemView: View) : ViewHolder(itemView)
    inner class RightTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextRightBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            binding.itemChatRightTextMessage.text = chatMessageModel.text
            binding.itemChatRightTextTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.time)

            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatMessageStatusIv)

            val senderUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemChatMessageStatusIv.visibility =
                if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    inner class LeftTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextLeftBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatLeftIv)

            binding.itemChatLeftTextMessage.text = chatMessageModel.text
            binding.itemChatLeftTextTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.time)
        }
    }

    inner class RightImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageRightBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {

            Glide
                .with(itemView.context)
                .load(chatMessageModel.image)
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatImageRightIv)

            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatMessageStatusIv)

            val senderUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemChatMessageStatusIv.visibility =
                if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            binding.itemChatRightImageTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.time)

            binding.itemChatImageRightIv.setOnClickListener {
                chatMessageClickInterface.onImageClicked(
                    chatMessageModel,
                    binding.itemChatImageRightIv
                )
            }
        }
    }

    inner class LeftImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageLeftBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatLeftIv)

            Glide
                .with(itemView.context)
                .load(chatMessageModel.image)
                .into(binding.itemChatLeftImage)

            binding.itemChatLeftImageTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.time)

            binding.itemChatLeftImage.setOnClickListener {
                chatMessageClickInterface.onImageClicked(
                    chatMessageModel,
                    binding.itemChatLeftImage
                )
            }
        }
    }

    class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessageModel>() {
        override fun areItemsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}