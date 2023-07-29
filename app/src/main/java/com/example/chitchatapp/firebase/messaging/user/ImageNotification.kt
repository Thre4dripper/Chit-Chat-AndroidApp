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

class ImageNotification {
    companion object {
        fun sendImageNotification(
            context: Context,
            firestore: FirebaseFirestore,
            chatId: String,
            imageUrl: String,
            from: String,
            to: String
        ) {
            var senderDetails: UserModel? = null
            var receiverDetails: UserModel? = null
            GetDetails.getUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId, imageUrl)
            }
            GetDetails.getUserDetails(firestore, to) {
                receiverDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId, imageUrl)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            receiverDetails: UserModel?,
            chatId: String,
            imageUrl: String
        ) {
            if (senderDetails == null || receiverDetails == null) return

            val payload = JSONObject()
            payload.put(NotificationConstants.TO, receiverDetails.fcmToken)

            val data = JSONObject()
            data.put(NotificationConstants.CHAT_TYPE, ChatType.USER)
            data.put(NotificationConstants.MESSAGE_TYPE, ChatMessageType.TypeImage)
            data.put(NotificationConstants.NOTIFIER_NAME, senderDetails.username)
            data.put(NotificationConstants.NOTIFIER_ID, senderDetails.uid)
            data.put(NotificationConstants.NOTIFIER_IMAGE, senderDetails.profileImage)
            data.put(ChatConstants.CHAT_ID, chatId)
            data.put(NotificationConstants.IMAGE, imageUrl)

            payload.put(NotificationConstants.DATA, data)

            Messaging.fireNotification(context, payload)
        }
    }
}