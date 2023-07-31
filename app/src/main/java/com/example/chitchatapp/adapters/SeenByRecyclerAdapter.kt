package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.SeenByClickInterface
import com.example.chitchatapp.databinding.ItemSeenByBinding
import com.example.chitchatapp.models.GroupChatUserModel

class SeenByRecyclerAdapter(private var seenByClickInterface: SeenByClickInterface) :
    ListAdapter<GroupChatUserModel, SeenByRecyclerAdapter.SeenByViewHolder>(SeenByDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeenByViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seen_by, parent, false)

        return SeenByViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeenByViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SeenByViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSeenByBinding.bind(itemView)

        fun bind(groupChatUserModel: GroupChatUserModel) {
            Glide.with(itemView).load(groupChatUserModel.profileImage)
                .placeholder(R.drawable.ic_profile).circleCrop()
                .into(binding.itemSeenByProfileUserIv)


            binding.itemSeenByProfileUserNameTv.text = groupChatUserModel.username
            binding.root.setOnClickListener {
                seenByClickInterface.onSeenByClicked(
                    groupChatUserModel.username, binding.itemSeenByProfileUserIv
                )
            }

        }
    }

    class SeenByDiffUtil : DiffUtil.ItemCallback<GroupChatUserModel>() {
        override fun areItemsTheSame(
            oldItem: GroupChatUserModel, newItem: GroupChatUserModel
        ): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(
            oldItem: GroupChatUserModel, newItem: GroupChatUserModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}