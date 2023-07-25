package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemChatTextRightBinding
import com.example.chitchatapp.databinding.ItemGroupCreatedBinding
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupMessageModel
import java.text.SimpleDateFormat
import java.util.Locale

class GroupChatRecyclerAdapter(
    private var loggedInUsername: String,
    private var groupChatModel: GroupChatModel
) :
    ListAdapter<GroupMessageModel, ViewHolder>(GroupChatDiffUtil()) {

    companion object {
        private const val VIEW_TYPE_CREATED_GROUP = 0
        private const val VIEW_TYPE_RIGHT_MESSAGE = 1
        private const val VIEW_TYPE_LEFT_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            VIEW_TYPE_CREATED_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_created, parent, false)
                return GroupCreatedViewHolder(view)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_right, parent, false)
                return RightTextViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupMessageModel = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_CREATED_GROUP -> {
                val groupCreatedViewHolder = holder as GroupCreatedViewHolder
                groupCreatedViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val groupMessageViewHolder = holder as RightTextViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            GroupMessageType.TypeCreatedGroup -> VIEW_TYPE_CREATED_GROUP
            GroupMessageType.TypeMessage -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_MESSAGE
                } else {
                    VIEW_TYPE_LEFT_MESSAGE
                }
            }

            else -> null!!
        }
    }

    inner class GroupCreatedViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemGroupCreatedBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val context = itemView.context
            val date = groupMessageModel.time.toDate()
            val formattedDate = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
            binding.itemGroupCreatedTv.text = context.getString(
                R.string.item_group_chat_group_created,
                groupMessageModel.from,
                formattedDate
            )
        }
    }

    inner class RightTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextRightBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            binding.itemChatRightTextMessage.text = groupMessageModel.text
            binding.itemChatRightTextTime.text =
                TimeUtils.getFormattedTime(groupMessageModel.time)

            val senderImage =
                ChatUtils.getGroupChatProfileImage(groupChatModel, groupMessageModel.from)
            Glide
                .with(itemView.context)
                .load(senderImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemChatMessageStatusIv)

            binding.itemChatMessageStatusIv.visibility = View.GONE
        }
    }

    class GroupChatDiffUtil : DiffUtil.ItemCallback<GroupMessageModel>() {
        override fun areItemsTheSame(
            oldItem: GroupMessageModel,
            newItem: GroupMessageModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GroupMessageModel,
            newItem: GroupMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}