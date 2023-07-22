package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.AddChatInterface
import com.example.chitchatapp.databinding.ItemAddChatResultBinding
import com.example.chitchatapp.models.UserModel

class AddChatsRecyclerAdapter(private var addChatInterface: AddChatInterface) :
    ListAdapter<UserModel, AddChatsRecyclerAdapter.SearchUsersViewHolder>(AddChatsDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUsersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_chat_result, parent, false)

        return SearchUsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchUsersViewHolder, position: Int) {
        val searchResult = getItem(position)
        holder.bind(searchResult)
    }

    inner class SearchUsersViewHolder(itemView: View) :
        ViewHolder(itemView) {
        private val binding = ItemAddChatResultBinding.bind(itemView)

        fun bind(user: UserModel) {
            val context = itemView.context
            Glide
                .with(context)
                .load(user.profileImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemAddChatsProfileImage)

            binding.itemAddChatsTextUsername.text =
                context.getString(R.string.item_add_chat_or_text_username, user.username)
            binding.itemAddChatsTextName.text = user.name

            binding.itemAddChatsMessageBtn.setOnClickListener {
                addChatInterface.onAddChat(user)
            }
        }
    }

    class AddChatsDiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
}