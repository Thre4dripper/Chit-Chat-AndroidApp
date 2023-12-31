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
import com.example.chitchatapp.adapters.interfaces.GroupMessageClickInterface
import com.example.chitchatapp.databinding.ItemChatImageLeftBinding
import com.example.chitchatapp.databinding.ItemChatImageRightBinding
import com.example.chitchatapp.databinding.ItemChatStickerLeftBinding
import com.example.chitchatapp.databinding.ItemChatStickerRightBinding
import com.example.chitchatapp.databinding.ItemChatTextLeftBinding
import com.example.chitchatapp.databinding.ItemChatTextRightBinding
import com.example.chitchatapp.databinding.ItemGroupNotifyMessageBinding
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupMessageModel
import java.text.SimpleDateFormat
import java.util.Locale

class GroupChatRecyclerAdapter(
    private var loggedInUsername: String,
    private var groupChatModel: GroupChatModel,
    private var groupMessageClickInterface: GroupMessageClickInterface
) : ListAdapter<GroupMessageModel, ViewHolder>(GroupChatDiffUtil()) {

    companion object {
        private const val VIEW_TYPE_CREATED_GROUP = 0
        private const val VIEW_TYPE_LEAVED_GROUP = 1
        private const val VIEW_TYPE_RIGHT_MESSAGE = 2
        private const val VIEW_TYPE_LEFT_MESSAGE = 3
        private const val VIEW_TYPE_RIGHT_IMAGE = 4
        private const val VIEW_TYPE_LEFT_IMAGE = 5
        private const val VIEW_TYPE_LEFT_STICKER = 6
        private const val VIEW_TYPE_RIGHT_STICKER = 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            VIEW_TYPE_CREATED_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_notify_message, parent, false)
                return GroupCreatedViewHolder(view)
            }

            VIEW_TYPE_LEAVED_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_notify_message, parent, false)
                return GroupLeavedViewHolder(view)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_right, parent, false)
                return RightTextViewHolder(view)
            }

            VIEW_TYPE_LEFT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_left, parent, false)
                return LeftTextViewHolder(view)
            }

            VIEW_TYPE_RIGHT_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_right, parent, false)
                return RightImageViewHolder(view)
            }

            VIEW_TYPE_LEFT_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_left, parent, false)
                return LeftImageViewHolder(view)
            }

            VIEW_TYPE_RIGHT_STICKER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sticker_right, parent, false)
                return RightStickerViewHolder(view)
            }

            VIEW_TYPE_LEFT_STICKER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sticker_left, parent, false)
                return LeftStickerViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupMessageModel = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_CREATED_GROUP -> {
                val groupNotifyViewHolder = holder as GroupCreatedViewHolder
                groupNotifyViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_LEAVED_GROUP -> {
                val groupNotifyViewHolder = holder as GroupLeavedViewHolder
                groupNotifyViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                val groupMessageViewHolder = holder as RightTextViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_LEFT_MESSAGE -> {
                val groupMessageViewHolder = holder as LeftTextViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_RIGHT_IMAGE -> {
                val groupMessageViewHolder = holder as RightImageViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_LEFT_IMAGE -> {
                val groupMessageViewHolder = holder as LeftImageViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_RIGHT_STICKER -> {
                val groupMessageViewHolder = holder as RightStickerViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            VIEW_TYPE_LEFT_STICKER -> {
                val groupMessageViewHolder = holder as LeftStickerViewHolder
                groupMessageViewHolder.bind(groupMessageModel)
            }

            else -> null!!
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            GroupMessageType.TypeCreatedGroup -> VIEW_TYPE_CREATED_GROUP
            GroupMessageType.TypeLeavedMember -> VIEW_TYPE_LEAVED_GROUP

            GroupMessageType.TypeText -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_MESSAGE
                } else {
                    VIEW_TYPE_LEFT_MESSAGE
                }
            }

            GroupMessageType.TypeImage -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_IMAGE
                } else {
                    VIEW_TYPE_LEFT_IMAGE
                }
            }

            GroupMessageType.TypeSticker -> {
                return if (item.from == loggedInUsername) {
                    VIEW_TYPE_RIGHT_STICKER
                } else {
                    VIEW_TYPE_LEFT_STICKER
                }
            }

            else -> null!!
        }
    }

    inner class GroupCreatedViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemGroupNotifyMessageBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val context = itemView.context
            val date = groupMessageModel.time.toDate()
            val formattedDate = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
            binding.itemGroupCreatedTv.text = context.getString(
                R.string.item_group_chat_group_created, groupMessageModel.from, "on $formattedDate"
            )
        }
    }

    inner class GroupLeavedViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemGroupNotifyMessageBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val context = itemView.context
            val date = groupMessageModel.time.toDate()
            val formattedDate = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
            binding.itemGroupCreatedTv.text = context.getString(
                R.string.item_group_chat_left_group, groupMessageModel.from, "on $formattedDate"
            )
        }
    }

    inner class RightTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextRightBinding.bind(itemView)
        private var statusIvs = listOf(
            binding.itemChatMessageStatusIv1,
            binding.itemChatMessageStatusIv2,
            binding.itemChatMessageStatusIv3,
            binding.itemChatMessageStatusIv4,
            binding.itemChatMessageStatusIv5
        )

        fun bind(groupMessageModel: GroupMessageModel) {
            binding.itemChatRightTextMessage.text = groupMessageModel.text
            binding.itemChatRightTextTime.text = TimeUtils.getFormattedTime(groupMessageModel.time)

            val senderImage =
                ChatUtils.getGroupChatProfileImage(groupChatModel, groupMessageModel.from)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatMessageStatusIv1)

            //take last 5 seen by except sender
            val lastFiveSeenBy =
                groupMessageModel.seenBy.filter { it != groupMessageModel.from }.takeLast(5)

            //hide all status image view initially
            repeat(5) {
                statusIvs[it].visibility = View.GONE
            }
            for (i in lastFiveSeenBy.indices) {
                statusIvs[i].visibility = View.VISIBLE
                val seenBy = lastFiveSeenBy[i]
                val seenByImage = ChatUtils.getGroupChatProfileImage(groupChatModel, seenBy)
                Glide.with(itemView.context).load(seenByImage).circleCrop()
                    .placeholder(R.drawable.ic_profile).into(statusIvs[i])

                statusIvs[i].setOnClickListener {
                    //anchor will be same for all status views
                    groupMessageClickInterface.onSeenByClicked(groupMessageModel, statusIvs[0])
                }
            }

            binding.root.setOnLongClickListener {
                groupMessageClickInterface.onSeenByClicked(
                    groupMessageModel,
                    binding.root
                )
                true
            }
        }
    }

    inner class LeftTextViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatTextLeftBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val senderImage =
                ChatUtils.getGroupChatProfileImage(groupChatModel, groupMessageModel.from)
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            binding.itemChatLeftTextMessage.text = groupMessageModel.text
            binding.itemChatLeftTextTime.text = TimeUtils.getFormattedTime(groupMessageModel.time)

            binding.itemChatLeftIv.setOnClickListener {
                groupMessageClickInterface.onUserImageClicked(
                    groupMessageModel.from, binding.itemChatLeftIv
                )
            }
        }
    }

    inner class RightImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageRightBinding.bind(itemView)
        private var statusIvs = listOf(
            binding.itemChatImageStatusIv1,
            binding.itemChatImageStatusIv2,
            binding.itemChatImageStatusIv3,
            binding.itemChatImageStatusIv4,
            binding.itemChatImageStatusIv5
        )

        fun bind(groupMessageModel: GroupMessageModel) {

            Glide.with(itemView.context).load(groupMessageModel.image)
                .placeholder(R.drawable.ic_profile).into(binding.itemChatImageRightIv)

            binding.itemChatRightImageTime.text = TimeUtils.getFormattedTime(groupMessageModel.time)

            binding.itemChatImageRightIv.setOnClickListener {
                groupMessageClickInterface.onImageClicked(
                    groupMessageModel, binding.itemChatImageRightIv
                )
            }

            //take last 5 seen by except sender
            val lastFiveSeenBy =
                groupMessageModel.seenBy.filter { it != groupMessageModel.from }.takeLast(5)

            //hide all status image view initially
            repeat(5) {
                statusIvs[it].visibility = View.GONE
            }
            for (i in lastFiveSeenBy.indices) {
                statusIvs[i].visibility = View.VISIBLE
                val seenBy = lastFiveSeenBy[i]
                val seenByImage = ChatUtils.getGroupChatProfileImage(groupChatModel, seenBy)
                Glide.with(itemView.context).load(seenByImage).circleCrop()
                    .placeholder(R.drawable.ic_profile).into(statusIvs[i])

                statusIvs[i].setOnClickListener {
                    //anchor will be same for all status views
                    groupMessageClickInterface.onSeenByClicked(groupMessageModel, statusIvs[0])
                }
            }

            binding.root.setOnLongClickListener {
                groupMessageClickInterface.onSeenByClicked(
                    groupMessageModel,
                    binding.root
                )
                true
            }
        }
    }

    inner class LeftImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatImageLeftBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val senderImage = ChatUtils.getGroupChatProfileImage(
                groupChatModel, groupMessageModel.from
            )
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            Glide.with(itemView.context).load(groupMessageModel.image)
                .into(binding.itemChatLeftImage)

            binding.itemChatLeftImageTime.text = TimeUtils.getFormattedTime(groupMessageModel.time)

            binding.itemChatLeftImage.setOnClickListener {
                groupMessageClickInterface.onImageClicked(
                    groupMessageModel, binding.itemChatLeftImage
                )
            }

            binding.itemChatLeftIv.setOnClickListener {
                groupMessageClickInterface.onUserImageClicked(
                    groupMessageModel.from, binding.itemChatLeftIv
                )
            }
        }
    }

    inner class RightStickerViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatStickerRightBinding.bind(itemView)
        private var statusIvs = listOf(
            binding.itemChatStickerStatusIv1,
            binding.itemChatStickerStatusIv2,
            binding.itemChatStickerStatusIv3,
            binding.itemChatStickerStatusIv4,
            binding.itemChatStickerStatusIv5
        )
        fun bind(groupMessageModel: GroupMessageModel) {
            binding.itemChatRightLottie.setAnimation(LottieStickers.getSticker(groupMessageModel.sticker!!))

            binding.itemChatRightStickerTime.text =
                TimeUtils.getFormattedTime(groupMessageModel.time)

            //take last 5 seen by except sender
            val lastFiveSeenBy =
                groupMessageModel.seenBy.filter { it != groupMessageModel.from }.takeLast(5)

            //hide all status image view initially
            repeat(5) {
                statusIvs[it].visibility = View.GONE
            }
            for (i in lastFiveSeenBy.indices) {
                statusIvs[i].visibility = View.VISIBLE
                val seenBy = lastFiveSeenBy[i]
                val seenByImage = ChatUtils.getGroupChatProfileImage(groupChatModel, seenBy)
                Glide.with(itemView.context).load(seenByImage).circleCrop()
                    .placeholder(R.drawable.ic_profile).into(statusIvs[i])

                statusIvs[i].setOnClickListener {
                    //anchor will be same for all status views
                    groupMessageClickInterface.onSeenByClicked(groupMessageModel, statusIvs[0])
                }
            }

            binding.root.setOnLongClickListener {
                groupMessageClickInterface.onSeenByClicked(
                    groupMessageModel,
                    binding.root
                )
                true
            }
        }
    }

    inner class LeftStickerViewHolder(itemView: View) : ViewHolder(itemView) {
        private var binding = ItemChatStickerLeftBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val senderImage = ChatUtils.getGroupChatProfileImage(
                groupChatModel, groupMessageModel.from
            )
            Glide.with(itemView.context).load(senderImage).circleCrop()
                .placeholder(R.drawable.ic_profile).into(binding.itemChatLeftIv)

            binding.itemChatLeftLottie.setAnimation(LottieStickers.getSticker(groupMessageModel.sticker!!))

            binding.itemChatLeftStickerTime.text =
                TimeUtils.getFormattedTime(groupMessageModel.time)

            binding.itemChatLeftIv.setOnClickListener {
                groupMessageClickInterface.onUserImageClicked(
                    groupMessageModel.from, binding.itemChatLeftIv
                )
            }
        }
    }

    class GroupChatDiffUtil : DiffUtil.ItemCallback<GroupMessageModel>() {
        override fun areItemsTheSame(
            oldItem: GroupMessageModel, newItem: GroupMessageModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GroupMessageModel, newItem: GroupMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}