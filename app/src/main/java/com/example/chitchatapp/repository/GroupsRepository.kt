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
import com.example.chitchatapp.firebase.groups.UpdateGroup
import com.example.chitchatapp.firebase.user.RemoveGroup
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

            val groupId = fireStore.collection(FirestoreCollections.GROUPS_COLLECTION).document().id

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
                    groupId,
                    groupName,
                    null,
                    loggedInUsername,
                    selectedGroupUsers,
                    onSuccess,
                )
                return
            }

            StorageUtils.getUrlFromStorage(
                storage, "${StorageFolders.GROUP_IMAGES_FOLDER}/${groupId}", groupImageUri
            ) { imageUrl ->
                CreateGroup.createNewGroup(
                    fireStore,
                    groupId,
                    groupName,
                    imageUrl,
                    loggedInUsername,
                    selectedGroupUsers,
                    onSuccess,
                )
            }
        }

        fun updateGroupImage(
            groupImage: Uri, groupId: String, callback: (String) -> Unit
        ) {
            val storage = FirebaseStorage.getInstance()

            StorageUtils.getUrlFromStorage(
                storage, "${StorageFolders.GROUP_IMAGES_FOLDER}/${groupId}", groupImage
            ) { imageUrl ->
                val fireStore = FirebaseFirestore.getInstance()
                UpdateGroup.updateGroupImage(
                    fireStore, groupId, imageUrl, callback
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

            RemoveGroup.removeFromUserCollection(
                fireStore,
                loggedInUsername,
                groupChatModel.id
            ) { success ->
                if (!success) {
                    onSuccess(false)
                    return@removeFromUserCollection
                }

                ExitGroup.exitFromGroup(
                    fireStore,
                    groupChatModel,
                    loggedInUsername,
                ) {
                    if (it == null) {
                        onSuccess(false)
                        return@exitFromGroup
                    }

                    if (it.members.isNotEmpty()) {
                        onSuccess(true)
                        return@exitFromGroup
                    }

                    //when group has no members, delete group

                    val storage = FirebaseStorage.getInstance()

                    DeleteGroup.deleteGroup(fireStore, groupChatModel, onSuccess)
                    DeleteGroup.deleteGroupImage(storage, groupChatModel.id) {}

                    val hasImages = groupChatModel.messages.any { message ->
                        message.type == GroupMessageType.TypeImage
                    }
                    if (!hasImages) {
                        onSuccess(true)
                        return@exitFromGroup
                    }
                    DeleteGroup.deleteGroupChatImages(storage, groupChatModel.id) {}
                    onSuccess(true)
                    return@exitFromGroup
                }
            }
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
            loggedInUsername: String, memberUsername: String, onSuccess: (String?) -> Unit
        ) {
            val firestore = FirebaseFirestore.getInstance()
            FindMemberChat.findChatId(firestore, loggedInUsername, memberUsername, onSuccess)
        }
    }
}