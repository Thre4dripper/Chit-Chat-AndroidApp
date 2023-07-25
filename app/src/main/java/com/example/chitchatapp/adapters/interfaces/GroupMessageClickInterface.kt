package com.example.chitchatapp.adapters.interfaces

import android.widget.ImageView
import com.example.chitchatapp.models.GroupMessageModel

interface GroupMessageClickInterface {
    fun onImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView)
}