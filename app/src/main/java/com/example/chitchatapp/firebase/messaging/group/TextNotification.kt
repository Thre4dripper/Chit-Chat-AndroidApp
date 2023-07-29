package com.example.chitchatapp.firebase.messaging.group

import android.content.Context
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.groups.GetGroupDetails
import com.example.chitchatapp.firebase.messaging.Messaging
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class TextNotification {
    companion object {
        private val TAG = "TextNotifications"
        fun sendTextNotification(
            context: Context,
            firestore: FirebaseFirestore,
            text: String,
            from: String,
            groupId: String
        ) {
            var senderDetails: UserModel? = null
            var groupDetails: GroupChatModel? = null
            val fcmTokens: List<String> = emptyList()

            GetDetails.getUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, groupDetails, fcmTokens, text)
            }
            GetGroupDetails.getDetails(firestore, groupId) { model, tokens ->
                groupDetails = model!!
                preparePayload(context, senderDetails, groupDetails, tokens, text)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            groupChatModel: GroupChatModel?,
            tokens: List<String>,
            text: String
        ) {
            if (senderDetails == null || groupChatModel == null) return
            // Send notification to all the members of the group
            tokens.forEach { token ->
                val payload = JSONObject()
                payload.put("to", token)

                val data = JSONObject()
                data.put("chatType", ChatType.GROUP)
                data.put("messageType", ChatMessageType.TypeText)
                data.put("notifierName", senderDetails.username)
                data.put("notifierImage", senderDetails.profileImage)
                data.put("groupId", groupChatModel.id)
                data.put("groupName", groupChatModel.name)
                data.put("groupImage", groupChatModel.image)
                data.put("text", text)

                payload.put("data", data)

                Messaging.fireNotification(context, payload)
            }
        }
    }
}