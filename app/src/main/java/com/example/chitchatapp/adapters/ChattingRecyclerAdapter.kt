package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.LottieStickers
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatMessageClickInterface
import com.example.chitchatapp.databinding.ItemChatImageLeftBinding
import com.example.chitchatapp.databinding.ItemChatImageRightBinding
import com.example.chitchatapp.databinding.ItemChatStickerLeftBinding
import com.example.chitchatapp.databinding.ItemChatStickerRightBinding
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
) : ListAdapter<ChatMessageModel, ViewHolder>(ChatMessageDiffCallback()) {
    private val TAG = "ChattingRecyclerAdapter"

    companion object {
        const val VIEW_TYPE_FIRST_MESSAGE = 0
        const val VIEW_TYPE_LEFT_MESSAGE = 1
        const val VIEW_TYPE_RIGHT_MESSAGE = 2
        const val VIEW_TYPE_LEFT_IMAGE = 3
        const val VIEW_TYPE_RIGHT_IMAGE = 4
        const val VIEW_TYPE_LEFT_STICKER = 5
        const val VIEW_TYPE_RIGHT_STICKER = 6
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

            VIEW_TYPE_RIGHT_STICKER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sticker_right, parent, false)
                RightStickerViewHolder(view)
            }

            VIEW_TYPE_LEFT_STICKER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sticker_left, parent, false)
                LeftStickerViewHolder(view)
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

            VIEW_TYPE_LEFT_STICKER -> {
                val viewHolder = holder as LeftStickerViewHolder
                viewHolder.bind(item)
            }

            VIEW_TYPE_RIGHT_STICKER -> {
                val viewHolder = holder as RightStickerViewHolder
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

            ChatMessageType.TypeText -> {
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

            ChatMessageType.TypeSticker -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_STICKER
                } else {
                    VIEW_TYPE_LEFT_STICKER
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
            binding.itemChatRightTextTime.text = TimeUtils.getFormattedTime(chatMessageModel.time)

            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatMessageStatusIv1)

            val senderUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemChatMessageStatusIv1.apply {
                visibility = if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setOnClickListener {
                    chatMessageClickInterface.onSeenByClicked(
                        chatMessageModel, binding.root
                    )
                }
            }

            binding.root.setOnLongClickListener {
                chatMessageClickInterface.onSeenByClicked(
                    chatMessageModel, binding.root
                )
                true
            }

            binding.itemChatMessageStatusIv2.visibility = View.GONE
            binding.itemChatMessageStatusIv3.visibility = View.GONE
            binding.itemChatMessageStatusIv4.visibility = View.GONE
            binding.itemChatMessageStatusIv5.visibility = View.GONE
        }
    }

    inner class LeftTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextLeftBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            binding.itemChatLeftTextMessage.text = chatMessageModel.text
            binding.itemChatLeftTextTime.text = TimeUtils.getFormattedTime(chatMessageModel.time)

            binding.itemChatLeftIv.setOnClickListener {
                chatMessageClickInterface.onUserImageClicked(
                    binding.itemChatLeftIv
                )
            }
        }
    }

    inner class RightImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageRightBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {

            Glide.with(itemView.context).load(chatMessageModel.image)
                .placeholder(R.drawable.ic_profile).into(binding.itemChatImageRightIv)

            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatImageStatusIv1)

            val senderUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemChatImageStatusIv1.apply {
                visibility = if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setOnClickListener {
                    chatMessageClickInterface.onSeenByClicked(
                        chatMessageModel, binding.root
                    )
                }
            }

            binding.root.setOnLongClickListener {
                chatMessageClickInterface.onSeenByClicked(
                    chatMessageModel, binding.root
                )
                true
            }

            binding.itemChatRightImageTime.text = TimeUtils.getFormattedTime(chatMessageModel.time)

            binding.itemChatImageRightIv.setOnClickListener {
                chatMessageClickInterface.onImageClicked(
                    chatMessageModel, binding.itemChatImageRightIv
                )
            }

            binding.itemChatImageStatusIv2.visibility = View.GONE
            binding.itemChatImageStatusIv3.visibility = View.GONE
            binding.itemChatImageStatusIv4.visibility = View.GONE
            binding.itemChatImageStatusIv5.visibility = View.GONE
        }
    }

    inner class LeftImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageLeftBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            Glide.with(itemView.context).load(chatMessageModel.image)
                .into(binding.itemChatLeftImage)

            binding.itemChatLeftImageTime.text = TimeUtils.getFormattedTime(chatMessageModel.time)

            binding.itemChatLeftImage.setOnClickListener {
                chatMessageClickInterface.onImageClicked(
                    chatMessageModel, binding.itemChatLeftImage
                )
            }

            binding.itemChatLeftIv.setOnClickListener {
                chatMessageClickInterface.onUserImageClicked(
                    binding.itemChatLeftIv
                )
            }
        }
    }

    inner class RightStickerViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemChatStickerRightBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            binding.itemChatRightLottie.setAnimation(LottieStickers.getSticker(chatMessageModel.sticker!!))
            binding.itemChatRightStickerTime.text =
                TimeUtils.getFormattedTime(chatMessageModel.time)

            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatStickerStatusIv1)

            val senderUsername = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemChatStickerStatusIv1.apply {
                visibility = if (chatMessageModel.seenBy.contains(senderUsername)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setOnClickListener {
                    chatMessageClickInterface.onSeenByClicked(
                        chatMessageModel, binding.root
                    )
                }
            }

            binding.root.setOnLongClickListener {
                chatMessageClickInterface.onSeenByClicked(
                    chatMessageModel, binding.root
                )
                true
            }


            binding.itemChatStickerStatusIv2.visibility = View.GONE
            binding.itemChatStickerStatusIv3.visibility = View.GONE
            binding.itemChatStickerStatusIv4.visibility = View.GONE
            binding.itemChatStickerStatusIv5.visibility = View.GONE
        }
    }

    inner class LeftStickerViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemChatStickerLeftBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val senderImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            binding.itemChatLeftLottie.setAnimation(LottieStickers.getSticker(chatMessageModel.sticker!!))

            binding.itemChatLeftStickerTime.text = TimeUtils.getFormattedTime(chatMessageModel.time)
        }
    }

    class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessageModel>() {
        override fun areItemsTheSame(
            oldItem: ChatMessageModel, newItem: ChatMessageModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ChatMessageModel, newItem: ChatMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}