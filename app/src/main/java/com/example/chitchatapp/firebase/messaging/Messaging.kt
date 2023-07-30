package com.example.chitchatapp.firebase.messaging

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.FirestoreCollections
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.models.ChatModel
import com.example.chitchatapp.models.GroupChatModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONObject

class Messaging {
    companion object {
        private val TAG = "Messaging"
        fun fireNotification(context: Context, payload: JSONObject) {
            val queue = Volley.newRequestQueue(context)
            val url = NotificationConstants.FCM_URL

            val request = object : JsonObjectRequest(Method.POST, url, payload, { response ->
                Log.d(TAG, "$response")
            }, { err ->
                Log.d(TAG, "${err.message}")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "key=${context.getString(R.string.fcm_server_key)}"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        }

        fun muteUnMuteUserNotifications(
            firestore: FirebaseFirestore,
            chatModel: ChatModel,
            loggedInUsername: String,
            mute: Boolean,
            onSuccess: (Boolean) -> Unit,
        ) {
            val updatedChatModel = chatModel.copy(
                mutedBy = if (mute) {
                    chatModel.mutedBy + loggedInUsername
                } else {
                    chatModel.mutedBy - loggedInUsername
                }
            )

            firestore.collection(FirestoreCollections.CHATS_COLLECTION)
                .document(chatModel.chatId)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }

        fun muteUnMuteGroupNotifications(
            firestore: FirebaseFirestore,
            groupChatModel: GroupChatModel,
            loggedInUsername: String,
            mute: Boolean,
            onSuccess: (Boolean) -> Unit,
        ) {
            val updatedChatModel = groupChatModel.copy(
                mutedBy = if (mute) {
                    groupChatModel.mutedBy + loggedInUsername
                } else {
                    groupChatModel.mutedBy - loggedInUsername
                }
            )

            firestore.collection(FirestoreCollections.GROUPS_COLLECTION)
                .document(groupChatModel.id)
                .set(updatedChatModel, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess(true)
                }
                .addOnFailureListener {
                    onSuccess(false)
                }
        }
    }
}