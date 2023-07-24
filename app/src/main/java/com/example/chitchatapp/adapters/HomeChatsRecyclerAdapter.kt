package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatClickInterface
import com.example.chitchatapp.databinding.ItemHomeChatBinding
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.enums.HomeLayoutType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatGroupModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.HomeChatModel

class HomeChatsRecyclerAdapter(
    private var loggedInUsername: String,
    private var chatClickInterface: ChatClickInterface
) :
    ListAdapter<HomeChatModel, ViewHolder>(ChatsDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_GROUP = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_home_chat, parent, false)
                return UserChatViewHolder(view)
            }

            VIEW_TYPE_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_home_chat, parent, false)
                return GroupChatViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val homeChatModel = getItem(position)
        when (getItemViewType(position)) {
            VIEW_TYPE_USER -> {
                (holder as UserChatViewHolder).bind(homeChatModel.userChat!!)
            }

            VIEW_TYPE_GROUP -> {
                (holder as GroupChatViewHolder).bind(homeChatModel.groupChat!!)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val homeChatModel = getItem(position)
        return when (homeChatModel.type) {
            HomeLayoutType.USER -> VIEW_TYPE_USER
            HomeLayoutType.GROUP -> VIEW_TYPE_GROUP
            else -> -1
        }
    }

    inner class UserChatViewHolder(itemView: View) : ViewHolder(itemView) {
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
                    TimeUtils.getFormattedTime(chatModel.chatMessages.last().time)

                if (chatModel.chatMessages.last().type == ChatMessageType.TypeMessage) {
                    itemHomeChatMessagePhoto.visibility = View.GONE
                    itemHomeChatMessage.text = chatModel.chatMessages.last().text?.trim() ?: ""
                } else if (chatModel.chatMessages.last().type == ChatMessageType.TypeImage) {
                    itemHomeChatMessagePhoto.visibility = View.VISIBLE
                    itemHomeChatMessage.text = chatModel.chatMessages.last().text?.trim() ?: "Photo"
                }

                val unreadMessages = chatModel.chatMessages.filter {
                    !it.seenBy.contains(loggedInUsername)
                }.size
                itemHomeChatCountTv.text = unreadMessages.toString()
                itemHomeChatCountCv.visibility = if (unreadMessages > 0) View.VISIBLE else View.GONE

            }

            binding.root.setOnClickListener {
                chatClickInterface.onChatClicked(chatModel.chatId)
            }
        }
    }

    inner class GroupChatViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemHomeChatBinding.bind(itemView)

        fun bind(groupChatModel: ChatGroupModel) {
            val context = itemView.context

            val groupImage = groupChatModel.image

            if (groupImage == null)
                binding.itemHomeChatProfileImage.imageTintList =
                    ContextCompat.getColorStateList(context, R.color.fabColor)

            Glide.with(context)
                .load(groupImage)
                .placeholder(R.drawable.ic_group)
                .circleCrop()
                .into(binding.itemHomeChatProfileImage)

            binding.apply {

                itemHomeChatUsername.text = groupChatModel.name
                itemHomeChatMessageTime.text =
                    TimeUtils.getFormattedTime(groupChatModel.messages.last().time)

                if (groupChatModel.messages.last().type == GroupMessageType.TypeMessage) {
                    itemHomeChatMessagePhoto.visibility = View.GONE
                    itemHomeChatMessage.text = groupChatModel.messages.last().text?.trim() ?: ""
                } else if (groupChatModel.messages.last().type == GroupMessageType.TypeImage) {
                    itemHomeChatMessagePhoto.visibility = View.VISIBLE
                    itemHomeChatMessage.text =
                        groupChatModel.messages.last().text?.trim() ?: "Photo"
                }

                val unreadMessages = groupChatModel.messages.filter {
                    !it.seenBy.contains(loggedInUsername)
                }.size
                itemHomeChatCountTv.text = unreadMessages.toString()
                itemHomeChatCountCv.visibility =
                    if (unreadMessages > 0) View.VISIBLE else View.GONE

            }

            //TODO: on click on group chat
//            binding.root.setOnClickListener {
//                chatClickInterface.onChatClicked(groupChatModel.id)
//            }
        }
    }

    class ChatsDiffCallback : DiffUtil.ItemCallback<HomeChatModel>() {
        override fun areItemsTheSame(oldItem: HomeChatModel, newItem: HomeChatModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: HomeChatModel,
            newItem: HomeChatModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}