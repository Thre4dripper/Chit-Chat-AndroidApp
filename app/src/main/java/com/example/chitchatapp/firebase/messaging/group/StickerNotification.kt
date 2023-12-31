package com.example.chitchatapp.firebase.messaging.group

import android.content.Context
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.groups.GetGroupDetails
import com.example.chitchatapp.firebase.messaging.Messaging
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.GroupChatModel
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class StickerNotification {
    companion object {
        fun sendStickerNotification(
            context: Context,
            firestore: FirebaseFirestore,
            from: String,
            groupId: String
        ) {
            var senderDetails: UserModel? = null
            var groupDetails: GroupChatModel? = null
            val fcmTokens: List<String> = emptyList()

            GetDetails.getUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, groupDetails, fcmTokens)
            }
            GetGroupDetails.getDetails(firestore, groupId) { model, tokens ->
                groupDetails = model!!
                preparePayload(context, senderDetails, groupDetails, tokens)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            groupChatModel: GroupChatModel?,
            tokens: List<String>,
        ) {
            if (senderDetails == null || groupChatModel == null) return
            // Send notification to all the members of the group
            tokens.forEach { token ->
                val payload = JSONObject()
                payload.put(NotificationConstants.NOTIFICATION_TOKEN, token)

                val data = JSONObject()
                data.put(NotificationConstants.CHAT_TYPE, ChatType.GROUP)
                data.put(NotificationConstants.MESSAGE_TYPE, ChatMessageType.TypeSticker)
                data.put(NotificationConstants.NOTIFIER_NAME, senderDetails.username)
                data.put(NotificationConstants.NOTIFIER_IMAGE, senderDetails.profileImage)
                data.put(GroupConstants.GROUP_ID, groupChatModel.id)
                data.put(GroupConstants.GROUP_NAME, groupChatModel.name)
                data.put(GroupConstants.GROUP_IMAGE, groupChatModel.image)

                payload.put(NotificationConstants.NOTIFICATION_DATA, data)

                Messaging.fireNotification(context, payload)
            }
        }
    }
}