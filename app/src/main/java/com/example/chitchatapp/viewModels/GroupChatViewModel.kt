package com.example.chitchatapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.store.UserStore

class GroupChatViewModel : ViewModel() {
    fun getLoggedInUsername(context: Context): String? {
        return UserStore.getUsername(context)
    }
}