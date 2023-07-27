package com.example.chitchatapp.firebase.profile

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.ErrorMessages
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.SuccessMessages
import com.example.chitchatapp.constants.UserConstants
import com.example.chitchatapp.firebase.utils.CrudUtils
import com.example.chitchatapp.firebase.utils.Utils
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.GroupChatUserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UpdateProfile {
    companion object {
        /**
         * Function to update username
         */
        fun updateUsername(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            prevUsername: String?,
            username: String,
            callback: (String) -> Unit
        ) {
            Utils.checkAvailableUsername(firestore, username) { isAvailable ->
                if (!isAvailable) {
                    callback(ErrorMessages.USERNAME_ALREADY_EXISTS)
                    return@checkAvailableUsername
                }

                getDataFromUsernameDocument(firestore, prevUsername!!) { userMap ->
                    if (userMap == null) {
                        callback(ErrorMessages.ERROR_UPDATING_USERNAME)
                        return@getDataFromUsernameDocument
                    }
                    userMap[UserConstants.USERNAME] = username

                    //create new document with username as document id
                    createNewUsernameDocument(firestore, username, userMap) { isCreated ->
                        if (isCreated) {

                            //delete previous username document
                            deletePreviousUsernameDocument(firestore, prevUsername) { isDeleted ->
                                if (isDeleted) {

                                    //update registered uid collection
                                    updateUIDCollection(firestore, user, username) { isUpdated ->
                                        callback(
                                            if (isUpdated) SuccessMessages.USERNAME_UPDATED_SUCCESSFULLY
                                            else ErrorMessages.ERROR_UPDATING_USERNAME
                                        )
                                    }
                                } else {
                                    callback(ErrorMessages.ERROR_UPDATING_USERNAME)
                                }
                            }
                        }
                    }
                }
            }
        }


        /**
         * PRIVATE FUNCTIONS POINTING TO CRUD UTILS
         */
        private fun getDataFromUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            callback: (HashMap<String, Any>?) -> Unit
        ) {
            CrudUtils.getFirestoreDocument(
                firestore,
                FirestoreCollections.USERS_COLLECTION,
                username
            ) { userMap ->
                callback(userMap)
            }
        }

        private fun createNewUsernameDocument(
            firestore: FirebaseFirestore,
            username: String,
            userMap: HashMap<String, Any>,
            callback: (Boolean) -> Unit
        ) {
            CrudUtils.createFirestoreDocument(
                firestore,
                FirestoreCollections.USERS_COLLECTION,
                username,
                userMap
            ) { isCreated ->
                callback(isCreated)
            }
        }

        private fun deletePreviousUsernameDocument(
            firestore: FirebaseFirestore, prevUsername: String, callback: (Boolean) -> Unit
        ) {
            CrudUtils.deleteFirestoreDocument(
                firestore,
                FirestoreCollections.USERS_COLLECTION,
                prevUsername
            ) { isDeleted ->
                callback(isDeleted)
            }
        }

        private fun updateUIDCollection(
            firestore: FirebaseFirestore,
            user: FirebaseUser,
            username: String,
            callback: (Boolean) -> Unit
        ) {
            val data = hashMapOf<String, Any>()
            data[UserConstants.USERNAME] = username
            CrudUtils.updateFirestoreDocument(
                firestore,
                FirestoreCollections.REGISTERED_IDS_COLLECTION,
                user.uid,
                data
            ) { isUpdated ->
                callback(isUpdated)
            }
        }

        /**
         * Function to update name
         */
        fun updateName(
            firestore: FirebaseFirestore,
            username: String,
            name: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION).document(username)
                .update(UserConstants.NAME, name).addOnSuccessListener {
                    callback(SuccessMessages.NAME_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(ErrorMessages.ERROR_UPDATING_NAME)
                }
        }

        /**
         * Function to update bio
         */
        fun updateBio(
            firestore: FirebaseFirestore,
            username: String,
            bio: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION).document(username)
                .update(UserConstants.BIO, bio).addOnSuccessListener {
                    callback(SuccessMessages.BIO_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(ErrorMessages.ERROR_UPDATING_BIO)
                }
        }

        /**
         * Function to update profile picture
         */
        fun updateProfilePicture(
            firestore: FirebaseFirestore,
            username: String,
            prevProfilePicture: String,
            profilePicture: String,
            callback: (String) -> Unit
        ) {
            firestore.collection(FirestoreCollections.USERS_COLLECTION).document(username)
                .update(UserConstants.PROFILE_IMAGE, profilePicture)
                .addOnSuccessListener {
                    callback(SuccessMessages.PROFILE_PICTURE_UPDATED_SUCCESSFULLY)
                }.addOnFailureListener {
                    callback(ErrorMessages.ERROR_UPDATING_PROFILE_PICTURE)
                }

            updateInChats(
                firestore,
                username,
                profilePicture
            )
            updateInGroups(
                firestore,
                username,
                prevProfilePicture,
                profilePicture
            )
        }

        private fun updateInChats(
            firestore: FirebaseFirestore,
            username: String,
            profilePicture: String,
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .where(
                    Filter.or(
                        Filter.equalTo(
                            //dmChatUser1.username
                            "${ChatConstants.DM_CHAT_USER_1}.${UserConstants.USERNAME}",
                            username
                        ),
                        Filter.equalTo(
                            //dmChatUser2.username
                            "${ChatConstants.DM_CHAT_USER_2}.${UserConstants.USERNAME}",
                            username
                        )
                    )
                )
                .get()
                .addOnSuccessListener {
                    val chatsList = mutableListOf<ChatModel>()
                    for (doc in it) {
                        val chat = doc.toObject(ChatModel::class.java)
                        chatsList.add(chat)
                    }
                    for (chat in chatsList) {
                        val updatedChat = chat.copy(
                            dmChatUser1 = if (chat.dmChatUser1.username == username) {
                                chat.dmChatUser1.copy(profileImage = profilePicture)
                            } else {
                                chat.dmChatUser1
                            },
                            dmChatUser2 = if (chat.dmChatUser2.username == username) {
                                chat.dmChatUser2.copy(profileImage = profilePicture)
                            } else {
                                chat.dmChatUser2
                            }
                        )

                        firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                            .document(chat.chatId)
                            .set(updatedChat, SetOptions.merge())
                    }
                }
        }

        private fun updateInGroups(
            firestore: FirebaseFirestore,
            username: String,
            prevProfilePicture: String,
            profilePicture: String,
        ) {
            val groupChatUserModel = GroupChatUserModel(
                username = username,
                profileImage = prevProfilePicture
            )
            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .whereArrayContains(GroupConstants.GROUP_MEMBERS, groupChatUserModel)
                .get()
                .addOnSuccessListener {
                    val groupsList = mutableListOf<GroupChatModel>()
                    for (doc in it) {
                        val group = doc.toObject(GroupChatModel::class.java)
                        groupsList.add(group)
                    }
                    for (group in groupsList) {
                        val updatedGroup = group.copy(
                            members = group.members.map { member ->
                                if (member.username == username) {
                                    member.copy(profileImage = profilePicture)
                                } else {
                                    member
                                }
                            }
                        )

                        firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                            .document(group.id)
                            .set(updatedGroup, SetOptions.merge())
                    }
                }
        }
    }
}