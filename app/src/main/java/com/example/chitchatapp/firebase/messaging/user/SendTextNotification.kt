package com.example.chitchatapp.firebase.messaging.user

import android.content.Context
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.messaging.Messaging
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class SendTextNotification {
    companion object {
        private val TAG = "SendUserNotifications"
        fun sendTextNotification(
            context: Context,
            firestore: FirebaseFirestore,
            text: String,
            from: String,
            to: String
        ) {
            var senderDetails: UserModel? = null
            var receiverDetails: UserModel? = null
            GetDetails.getLiveUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, text)
            }
            GetDetails.getLiveUserDetails(firestore, to) {
                receiverDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, text)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            receiverDetails: UserModel?,
            text: String
        ) {
            if (senderDetails == null || receiverDetails == null) return

            val payload = JSONObject()
            payload.put("to", receiverDetails.fcmToken)

            val body = JSONObject()
            body.put("chatType", ChatType.USER)
            body.put("messageType", ChatMessageType.TypeText)
            body.put("notifierName", senderDetails.username)
            body.put("notifierId", senderDetails.uid)
            body.put("notifierImage", senderDetails.profileImage)
            body.put("text", text)

//            val notification = JSONObject()
//            notification.put("data", body)

            payload.put("data", body)

            Messaging.fireNotification(context, payload)
        }
    }
}