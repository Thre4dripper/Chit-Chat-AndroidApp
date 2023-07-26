package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.databinding.ItemHomeFavouriteBinding
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.HomeChatModel

class HomeFavRecyclerAdapter(private var loggedInUsername: String) :
    ListAdapter<HomeChatModel, HomeFavRecyclerAdapter.FavouritesViewHolder>(ChatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_favourite, parent, false)

        return FavouritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val homeChatModel = getItem(position)
        //this will never be null
        holder.bind(homeChatModel.userChat!!)
    }

    inner class FavouritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemHomeFavouriteBinding.bind(itemView)

        fun bind(chatModel: ChatModel) {
            val context = itemView.context

            val profileImage = ChatUtils.getUserChatProfileImage(chatModel, loggedInUsername)
            Glide.with(context)
                .load(profileImage)
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemHomeFavIv)

            val username = ChatUtils.getUserChatUsername(chatModel, loggedInUsername)
            binding.itemHomeFavTv.text = username
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