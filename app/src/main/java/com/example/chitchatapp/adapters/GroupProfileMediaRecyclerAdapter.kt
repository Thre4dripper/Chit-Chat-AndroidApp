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
import com.example.chitchatapp.databinding.ItemProfileMediaBinding
import com.example.chitchatapp.models.GroupMessageModel

class GroupProfileMediaRecyclerAdapter(private var groupProfileClickInterface: GroupProfileClickInterface) :
    ListAdapter<GroupMessageModel, GroupProfileMediaRecyclerAdapter.MediaViewHolder>(
        GroupMediasDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val chatMessageModel = getItem(position)
        holder.bind(chatMessageModel)
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProfileMediaBinding.bind(itemView)

        fun bind(groupMessageModel: GroupMessageModel) {
            val context = itemView.context

            Glide.with(context)
                .load(groupMessageModel.image)
                .into(binding.itemChatProfileMedia)

            binding.root.setOnClickListener {
                groupProfileClickInterface.onMediaImageClicked(
                    groupMessageModel,
                    binding.itemChatProfileMedia
                )
            }
        }
    }

    class GroupMediasDiffCallback : DiffUtil.ItemCallback<GroupMessageModel>() {
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