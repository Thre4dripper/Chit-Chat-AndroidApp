package com.example.chitchatapp.firebase.groups

import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.UserConstants
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class FindMemberChat {
    companion object {
        fun findChatId(
            firestore: FirebaseFirestore,
            loggedInUsername: String,
            memberUsername: String,
            onSuccess: (String?) -> Unit
        ) {
            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo(
                                //dmChatUser1.username
                                "${ChatConstants.DM_CHAT_USER_1}.${UserConstants.USERNAME}",
                                loggedInUsername
                            ),
                            Filter.equalTo(
                                //dmChatUser2.username
                                "${ChatConstants.DM_CHAT_USER_2}.${UserConstants.USERNAME}",
                                memberUsername
                            )
                        ),
                        Filter.and(
                            Filter.equalTo(
                                //dmChatUser1.username
                                "${ChatConstants.DM_CHAT_USER_1}.${UserConstants.USERNAME}",
                                memberUsername
                            ),
                            Filter.equalTo(
                                //dmChatUser2.username
                                "${ChatConstants.DM_CHAT_USER_2}.${UserConstants.USERNAME}",
                                loggedInUsername
                            )
                        )
                    )
                )
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        onSuccess(null)
                        return@addOnSuccessListener
                    }
                    onSuccess(documents.documents[0].id)
                }
        }
    }
}