package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.GroupProfileClickInterface
import com.example.chitchatapp.databinding.ItemGroupProfileMemberBinding
import com.example.chitchatapp.models.GroupChatUserModel

class GroupMembersRecyclerAdapter(private var groupProfileClickInterface: GroupProfileClickInterface) :
    ListAdapter<GroupChatUserModel, GroupMembersRecyclerAdapter.GroupMemberViewHolder>(
        GroupMemberDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group_profile_member, parent, false)
        return GroupMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemGroupProfileMemberBinding.bind(itemView)

        fun bind(groupChatUserModel: GroupChatUserModel) {
            Glide.with(itemView)
                .load(groupChatUserModel.profileImage)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.itemGroupProfileUserIv)

            binding.itemGroupProfileUserNameTv.text = groupChatUserModel.username

            binding.root.setOnClickListener {
                groupProfileClickInterface.onGroupMemberClicked(groupChatUserModel.username)
            }
        }
    }

    class GroupMemberDiffUtil : DiffUtil.ItemCallback<GroupChatUserModel>() {
        override fun areItemsTheSame(
            oldItem: GroupChatUserModel,
            newItem: GroupChatUserModel
        ): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(
            oldItem: GroupChatUserModel,
            newItem: GroupChatUserModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}