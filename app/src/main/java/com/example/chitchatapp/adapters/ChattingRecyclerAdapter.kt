package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chitchatapp.R
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.models.ChatMessageModel

class ChattingRecyclerAdapter :
    ListAdapter<ChatMessageModel, ViewHolder>(ChatMessageDiffCallback()) {
    companion object {
        private const val VIEW_TYPE_HELLO_MESSAGE = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HELLO_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_hello_message, parent, false)
                HelloMessageViewHolder(view)
            }

            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_HELLO_MESSAGE -> {
                val viewHolder = holder as HelloMessageViewHolder
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.chatMessageType) {
            ChatMessageType.TypeFirstMessage -> VIEW_TYPE_HELLO_MESSAGE
            else -> -1
        }
    }

    inner class HelloMessageViewHolder(itemView: View) : ViewHolder(itemView)

    class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessageModel>() {
        override fun areItemsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem.chatMessageId == newItem.chatMessageId
        }

        override fun areContentsTheSame(
            oldItem: ChatMessageModel,
            newItem: ChatMessageModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}