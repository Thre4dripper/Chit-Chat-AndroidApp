package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.ChatMessageClickInterface
import com.example.chitchatapp.databinding.ItemChatProfileMediaBinding
import com.example.chitchatapp.models.ChatMessageModel

class ChatProfileMediaRecyclerAdapter(private var chatMessageClickInterface: ChatMessageClickInterface) :
    ListAdapter<ChatMessageModel, ChatProfileMediaRecyclerAdapter.MediaViewHolder>(ChatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_profile_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val chatMessageModel = getItem(position)
        holder.bind(chatMessageModel)
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemChatProfileMediaBinding.bind(itemView)

        fun bind(chatMessageModel: ChatMessageModel) {
            val context = itemView.context

            Glide.with(context)
                .load(chatMessageModel.image)
                .into(binding.itemChatProfileMedia)

            binding.root.setOnClickListener {
                chatMessageClickInterface.onImageClicked(
                    chatMessageModel,
                    binding.itemChatProfileMedia
                )
            }
        }
    }

    class ChatsDiffCallback : DiffUtil.ItemCallback<ChatMessageModel>() {
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