package com.example.chitchatapp.firebase.messaging.user

import android.content.Context
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.messaging.Messaging
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class TextNotification {
    companion object {
        private val TAG = "SendUserNotifications"
        fun sendTextNotification(
            context: Context,
            firestore: FirebaseFirestore,
            chatId: String,
            text: String,
            from: String,
            to: String
        ) {
            var senderDetails: UserModel? = null
            var receiverDetails: UserModel? = null
            GetDetails.getUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId, text)
            }
            GetDetails.getUserDetails(firestore, to) {
                receiverDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId, text)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            receiverDetails: UserModel?,
            chatId: String,
            text: String
        ) {
            if (senderDetails == null || receiverDetails == null) return

            val payload = JSONObject()
            payload.put(NotificationConstants.NOTIFICATION_TOKEN, receiverDetails.fcmToken)

            val data = JSONObject()
            data.put(NotificationConstants.CHAT_TYPE, ChatType.USER)
            data.put(NotificationConstants.MESSAGE_TYPE, ChatMessageType.TypeText)
            data.put(NotificationConstants.NOTIFIER_NAME, senderDetails.username)
            data.put(NotificationConstants.NOTIFIER_ID, senderDetails.uid)
            data.put(NotificationConstants.NOTIFIER_IMAGE, senderDetails.profileImage)
            data.put(ChatConstants.CHAT_ID, chatId)
            data.put(NotificationConstants.NOTIFICATION_TEXT, text)

            payload.put(NotificationConstants.NOTIFICATION_DATA, data)

            Messaging.fireNotification(context, payload)
        }
    }
}