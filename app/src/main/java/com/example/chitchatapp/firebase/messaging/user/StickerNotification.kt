package com.example.chitchatapp.firebase.messaging.user

import android.content.Context
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.firebase.messaging.Messaging
import com.example.chitchatapp.firebase.user.GetDetails
import com.example.chitchatapp.models.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class StickerNotification {
    companion object {
        fun sendStickerNotification(
            context: Context,
            firestore: FirebaseFirestore,
            chatId: String,
            from: String,
            to: String,
        ) {
            var senderDetails: UserModel? = null
            var receiverDetails: UserModel? = null
            GetDetails.getUserDetails(firestore, from) {
                senderDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId)
            }
            GetDetails.getUserDetails(firestore, to) {
                receiverDetails = it!!
                preparePayload(context, senderDetails, receiverDetails, chatId)
            }
        }

        private fun preparePayload(
            context: Context,
            senderDetails: UserModel?,
            receiverDetails: UserModel?,
            chatId: String
        ) {
            if (senderDetails == null || receiverDetails == null) return

            val payload = JSONObject()
            payload.put("to", receiverDetails.fcmToken)

            val data = JSONObject()
            data.put("chatType", ChatType.USER)
            data.put("messageType", ChatMessageType.TypeSticker)
            data.put("notifierName", senderDetails.username)
            data.put("notifierId", senderDetails.uid)
            data.put("notifierImage", senderDetails.profileImage)
            data.put("chatId", chatId)

            payload.put("data", data)

            Messaging.fireNotification(context, payload)
        }
    }
}