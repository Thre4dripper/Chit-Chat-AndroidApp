package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemGroupCreatedBinding
import com.example.chitchatapp.models.GroupMessageModel
import java.text.SimpleDateFormat
import java.util.Locale

class GroupChatRecyclerAdapter : ListAdapter<GroupMessageModel, ViewHolder>(GroupChatDiffUtil()) {

    companion object {
        private const val VIEW_TYPE_CREATED_GROUP = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            VIEW_TYPE_CREATED_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_created, parent, false)
                return GroupCreatedViewHolder(view)
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