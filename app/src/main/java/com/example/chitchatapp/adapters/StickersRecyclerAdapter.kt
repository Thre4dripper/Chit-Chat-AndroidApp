package com.example.chitchatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchatapp.R
import com.example.chitchatapp.adapters.interfaces.StickerClickInterface
import com.example.chitchatapp.databinding.ItemBsStickerBinding

class StickersRecyclerAdapter(private var stickerClickInterface: StickerClickInterface) :
    ListAdapter<Int, StickersRecyclerAdapter.StickerViewHolder>(StickerDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bs_sticker, parent, false)
        return StickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemBsStickerBinding.bind(itemView)

        fun bind(sticker: Int) {
            binding.bsStickerLottie.setAnimation(sticker)

            binding.root.setOnClickListener {
                stickerClickInterface.onStickerClick(sticker)
            }
        }
    }

    class StickerDiffUtil : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
}