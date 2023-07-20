package com.example.chitchatapp.adapters.interfaces

import com.example.chitchatapp.models.ChatMessageModel

interface ChatMessageClickInterface {
    fun onImageClicked(chatMessageModel: ChatMessageModel)
}