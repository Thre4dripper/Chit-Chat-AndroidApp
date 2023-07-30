package com.example.chitchatapp.adapters.interfaces

import android.widget.ImageView
import com.example.chitchatapp.models.GroupMessageModel

interface GroupProfileClickInterface {
    fun onMediaImageClicked(groupMessageModel: GroupMessageModel, chatImageIv: ImageView)
    fun onGroupMemberClicked(loggedInUsername: String, memberUsername: String, clickedIv: ImageView)
}