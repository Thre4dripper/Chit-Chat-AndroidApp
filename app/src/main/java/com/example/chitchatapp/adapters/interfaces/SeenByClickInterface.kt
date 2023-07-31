package com.example.chitchatapp.adapters.interfaces

import android.widget.ImageView

interface SeenByClickInterface {
    fun onSeenByClicked(seenByUsername: String, clickedIv: ImageView)
}