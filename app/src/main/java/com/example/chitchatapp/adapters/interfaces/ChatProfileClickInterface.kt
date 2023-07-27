package com.example.chitchatapp.adapters.interfaces

import android.widget.ImageView
import com.example.chitchatapp.models.ChatMessageModel

interface ChatProfileClickInterface {
    fun onMediaImageClicked(chatMessageModel: ChatMessageModel, chatImageIv: ImageView)
    fun onCommonGroupClicked(groupId: String)
}