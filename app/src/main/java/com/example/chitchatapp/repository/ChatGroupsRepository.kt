package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.firebase.groups.CreateGroup
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ChatGroupsRepository {
    companion object {
        fun createGroup(
            context: Context,
            groupName: String,
            groupImageUri: Uri?,
            selectedUsers: List<ChatModel>,
            onSuccess: (Boolean) -> Unit,
        ) {
            val fireStore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: return

            val selectedUsernames = selectedUsers.map {
                val username = ChatUtils.getChatUsername(it, loggedInUsername)
                username
            }.toMutableList()
            selectedUsernames.add(loggedInUsername)

            if (groupImageUri == null) {
                CreateGroup.createNewGroup(
                    fireStore,
                    groupName,
                    null,
                    loggedInUsername,
                    selectedUsernames,
                    onSuccess,
                )
                return
            }

            StorageUtils.getUrlFromStorage(
                storage,
                "${StorageFolders.GROUP_IMAGES_FOLDER}/${UUID.randomUUID()}",
                groupImageUri
            ) { imageUrl ->
                CreateGroup.createNewGroup(
                    fireStore,
                    groupName,
                    imageUrl,
                    loggedInUsername,
                    selectedUsernames,
                    onSuccess,
                )
            }
        }
    }
}