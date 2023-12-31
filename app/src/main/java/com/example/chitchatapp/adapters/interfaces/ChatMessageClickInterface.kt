package com.example.chitchatapp.adapters.interfaces

import android.view.View
import android.widget.ImageView
import com.example.chitchatapp.models.ChatMessageModel

interface ChatMessageClickInterface {
    fun onImageClicked(chatMessageModel: ChatMessageModel, chatImageIv: ImageView)
    fun onUserImageClicked(chatImageIv: ImageView)
    fun onSeenByClicked(chatMessageModel: ChatMessageModel, anchor: View)
}