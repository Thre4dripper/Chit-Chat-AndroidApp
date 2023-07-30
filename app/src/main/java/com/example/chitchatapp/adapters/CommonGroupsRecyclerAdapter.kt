package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatProfileClickInterface
import com.example.chitchatapp.databinding.ItemChatProfileCommonGroupBinding
import com.example.chitchatapp.models.GroupChatModel

class CommonGroupsRecyclerAdapter(private var chatProfileClickInterface: ChatProfileClickInterface) :
    ListAdapter<GroupChatModel, CommonGroupsRecyclerAdapter.CommonGroupViewHolder>(
        CommonGroupsDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_profile_common_group, parent, false)
        return CommonGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommonGroupViewHolder, position: Int) {
        val groupChatModel = getItem(position)
        holder.bind(groupChatModel)
    }

    inner class CommonGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemChatProfileCommonGroupBinding.bind(itemView)

        fun bind(groupChatModel: GroupChatModel) {
            val context = itemView.context

            val groupImage = groupChatModel.image
            if (groupImage == null) binding.itemCommonGroupImage.imageTintList =
                ContextCompat.getColorStateList(context, R.color.fabColor)

            Glide.with(context).load(groupChatModel.image).placeholder(R.drawable.ic_group)
                .circleCrop().into(binding.itemCommonGroupImage)

            binding.itemCommonGroupNameTv.text = groupChatModel.name

            var members = ""
            groupChatModel.members.forEach { member ->
                members += "${member.username}, "
            }

            //remove last comma
            members = members.substring(0, members.length - 2)

            binding.itemCommonGroupMembersTv.text = members

            binding.root.setOnClickListener {
                chatProfileClickInterface.onCommonGroupClicked(
                    groupChatModel.id, binding.itemCommonGroupImage
                )
            }
        }
    }

    class CommonGroupsDiffUtil : DiffUtil.ItemCallback<GroupChatModel>() {
        override fun areItemsTheSame(oldItem: GroupChatModel, newItem: GroupChatModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupChatModel, newItem: GroupChatModel): Boolean {
            return oldItem == newItem
        }
    }
}