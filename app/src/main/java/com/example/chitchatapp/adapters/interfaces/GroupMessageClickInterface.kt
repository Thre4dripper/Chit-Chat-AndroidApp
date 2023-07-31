package com.example.chitchatapp.adapters.interfaces

import android.view.View
import android.widget.ImageView
import com.example.chitchatapp.models.GroupMessageModel

interface GroupMessageClickInterface {
    fun onImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView)
    fun onUserImageClicked(clickedUsername: String, chatImageIv: ImageView)
    fun onSeenByClicked(groupMessageModel: GroupMessageModel, anchor: View)
}