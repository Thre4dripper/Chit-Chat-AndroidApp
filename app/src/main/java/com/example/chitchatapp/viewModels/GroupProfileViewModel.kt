package com.example.chitchatapp.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.UserModel
import com.example.chitchatapp.repository.GroupChatsRepository
import com.example.chitchatapp.repository.GroupsRepository
import com.example.chitchatapp.repository.UserRepository

class GroupProfileViewModel : ViewModel() {

    fun updateGroupImage(
        groupImage: Uri,
        groupId: String,
        callback: (String) -> Unit,
    ) = GroupsRepository.updateGroupImage(groupImage, groupId, callback)

    fun muteUnMuteGroup(
        groupChatModel: GroupChatModel,
        loggedInUsername: String,
        mute: Boolean,
        onSuccess: (Boolean) -> Unit
    ) = GroupChatsRepository.muteUnMuteGroup(groupChatModel, loggedInUsername, mute, onSuccess)

    fun findChatId(
        loggedInUsername: String,
        memberUsername: String,
        onSuccess: (String?) -> Unit
    ) = GroupsRepository.findMemberChatId(loggedInUsername, memberUsername, onSuccess)

    fun getUserModel(
        memberUsername: String,
        onSuccess: (UserModel?) -> Unit
    ) = UserRepository.listenUserDetails(memberUsername, onSuccess)
}