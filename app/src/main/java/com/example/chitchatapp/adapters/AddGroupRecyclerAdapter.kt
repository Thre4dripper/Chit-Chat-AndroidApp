package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.AddGroupInterface
import com.example.chitchatapp.databinding.ItemAddGroupChatBinding
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.TimeUtils
import com.example.chitchatapp.models.ChatModel
import com.google.firebase.Timestamp

class AddGroupRecyclerAdapter(
    private var loggedInUsername: String,
    private var selectedUsers: LiveData<List<ChatModel>?>,
    private var addGroupInterface: AddGroupInterface
) :
    ListAdapter<ChatModel, AddGroupRecyclerAdapter.SearchUsersViewHolder>(AddGroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUsersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_group_chat, parent, false)

        return SearchUsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchUsersViewHolder, position: Int) {
        val searchResult = getItem(position)
        holder.bind(searchResult)
    }

    inner class SearchUsersViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemAddGroupChatBinding.bind(itemView)

        fun bind(chatModel: ChatModel) {
            val context = itemView.context

            val profileImage = ChatUtils.getChatProfileImage(chatModel, loggedInUsername)
            Glide
                .with(context)
                .load(profileImage)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.itemAddGroupProfileImage)

            val username = ChatUtils.getChatUsername(chatModel, loggedInUsername)
            binding.itemAddGroupTextUsername.text =
                context.getString(R.string.item_add_chat_or_text_username, username)

            val status = ChatUtils.getChatStatus(chatModel, loggedInUsername)
            if (status == UserStatus.Online.name) {
                binding.itemAddGroupStatusCv.setCardBackgroundColor(
                    context.getColor(R.color.green)
                )

                binding.itemAddGroupTextStatus.text = status
            } else {
                binding.itemAddGroupStatusCv.setCardBackgroundColor(
                    context.getColor(R.color.yellow)
                )

                val statusTime = status.split(" ")[1]
                val time = TimeUtils.getFormattedTime(Timestamp(statusTime.toLong(), 0))
                binding.itemAddGroupTextStatus.text =
                    context.getString(R.string.chatting_activity_text_last_seen, time)
            }

            binding.itemAddGroupCvCheck.visibility =
                if (selectedUsers.value?.find { it.chatId == chatModel.chatId } != null) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                addGroupInterface.onUserClicked(chatModel.chatId)
            }
        }
    }

    class AddGroupDiffCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
}