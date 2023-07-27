package com.example.chitchatapp.repository

import android.content.Context
import android.net.Uri
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.enums.GroupMessageType
import com.example.chitchatapp.firebase.groups.CommonGroups
import com.example.chitchatapp.firebase.groups.CreateGroup
import com.example.chitchatapp.firebase.groups.DeleteGroup
import com.example.chitchatapp.firebase.groups.ExitGroup
import com.example.chitchatapp.firebase.groups.FindMemberChat
import com.example.chitchatapp.firebase.utils.ChatUtils
import com.example.chitchatapp.firebase.utils.StorageUtils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class GroupsRepository {
    companion object {
        private val TAG = "GroupsRepository"
        fun createGroup(
            context: Context,
            groupName: String,
            groupImageUri: Uri?,
            selectedUsers: List<ChatModel>,
            onSuccess: (String?) -> Unit,
        ) {
            val fireStore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: return

            val groupChatId =
                fireStore.collection(FirestoreCollections.GROUPS_COLLECTION).document().id

            val selectedGroupUsers = selectedUsers.map {
                val username = ChatUtils.getUserChatUsername(it, loggedInUsername)
                val profileImage = ChatUtils.getUserChatProfileImage(it, loggedInUsername)

                GroupChatUserModel(username, profileImage)
            }.toMutableList()

            //user details were fetched earlier
            val currentUserImage = UserRepository.userDetails.value?.profileImage ?: ""
            selectedGroupUsers.add(0, GroupChatUserModel(loggedInUsername, currentUserImage))

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

        fun exitGroup(
            context: Context,
            groupChatModel: GroupChatModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            val fireStore = FirebaseFirestore.getInstance()
            val loggedInUsername = UserStore.getUsername(context) ?: return

            val membersCount = groupChatModel.members.size

            // if only one member is left in the group, delete the group
            if (membersCount == 1) {
                val storage = FirebaseStorage.getInstance()

                DeleteGroup.deleteGroup(fireStore, groupChatModel, onSuccess)
                DeleteGroup.deleteGroupImage(storage, groupChatModel.id) {}

                val hasImages = groupChatModel.messages.any { message ->
                    message.type == GroupMessageType.TypeImage
                }
                if (!hasImages) {
                    onSuccess(true)
                    return
                }
                DeleteGroup.deleteGroupChatImages(storage, groupChatModel.id) {}
                onSuccess(true)
                return
            }

            //otherwise, exit the group
            ExitGroup.exitGroup(
                fireStore,
                groupChatModel,
                loggedInUsername,
                onSuccess,
            )
        }

        fun findCommonGroups(
            chatUsername: String,
            chatUserImage: String,
            loggedInUsername: String,
            onSuccess: (List<GroupChatModel>) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            val chatUserModel = GroupChatUserModel(chatUsername, chatUserImage)
            CommonGroups.findCommonGroups(firestore, chatUserModel, loggedInUsername, onSuccess)
        }

        fun findMemberChatId(
            loggedInUsername: String,
            memberUsername: String,
            onSuccess: (String?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            FindMemberChat.findChatId(firestore, loggedInUsername, memberUsername, onSuccess)
        }
    }
}