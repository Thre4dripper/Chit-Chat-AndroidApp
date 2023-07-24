package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.firebase.chats.GetGroupChats
import com.example.chitchatapp.firebase.groups.CreateGroup
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.ChatGroupModel
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class GroupsRepository {
    companion object {
        val groupChats = MutableLiveData<List<ChatGroupModel>?>(null)

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

            val groupChatId =
                fireStore.collection(FirestoreCollections.GROUPS_COLLECTION).document().id

            val selectedGroupUsers = selectedUsers.map {
                val username = ChatUtils.getChatUsername(it, loggedInUsername)
                val profileImage = ChatUtils.getChatProfileImage(it, loggedInUsername)

                GroupChatUserModel(username, profileImage)
            }.toMutableList()

            //user details were fetched earlier
            val currentUserImage = UserDetailsRepository.userDetails.value?.profileImage ?: ""
            selectedGroupUsers.add(GroupChatUserModel(loggedInUsername, currentUserImage))

            if (groupImageUri == null) {
                CreateGroup.createNewGroup(
                    fireStore,
                    groupChatId,
                    groupName,
                    null,
                    loggedInUsername,
                    selectedGroupUsers,
                    onSuccess,
                )
                return
            }

            StorageUtils.getUrlFromStorage(
                storage,
                "${StorageFolders.GROUP_IMAGES_FOLDER}/${groupChatId}",
                groupImageUri
            ) { imageUrl ->
                CreateGroup.createNewGroup(
                    fireStore,
                    groupChatId,
                    groupName,
                    imageUrl,
                    loggedInUsername,
                    selectedGroupUsers,
                    onSuccess,
                )
            }
        }

        fun getAllGroupChats(context: Context) {
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: ""
            val userImage = UserDetailsRepository.userDetails.value?.profileImage ?: ""

            val groupUserModel = GroupChatUserModel(loggedInUsername, userImage)
            GetGroupChats.getAllGroupChats(firestore, groupUserModel) {
                groupChats.value = it
            }
        }
    }
}