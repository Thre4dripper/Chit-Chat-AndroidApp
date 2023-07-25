package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.StorageFolders
import com.example.chitchatapp.models.GroupChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DeleteGroup {
    companion object {
        fun deleteGroup(
            fireStore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            onSuccess: (Boolean) -> Unit,
        ) {
            fireStore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .delete()
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun deleteGroupImage(
            storage: FirebaseStorage,
            groupId: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            //list all files in group folder
            storage.reference.child("${StorageFolders.GROUP_IMAGES_FOLDER}/${groupId}/")
                .listAll()
                .addOnSuccessListener { listResult ->
                    //delete all files in group folder
                    listResult.items.forEach { item ->
                        item.delete()
                    }

                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun deleteGroupChatImages(
            storage: FirebaseStorage,
            groupId: String,
            onSuccess: (Boolean) -> Unit,
        ) {
            //list all files in group folder
            storage.reference.child("${StorageFolders.CHAT_IMAGES_FOLDER}/${groupId}/")
                .listAll()
                .addOnSuccessListener { listResult ->
                    //delete all files in group folder
                    listResult.items.forEach { item ->
                        item.delete()
                    }

                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}