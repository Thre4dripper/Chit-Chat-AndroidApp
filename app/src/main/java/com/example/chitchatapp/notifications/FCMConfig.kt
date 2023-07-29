package com.example.chitchatapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.store.UserStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FCMConfig : FirebaseMessagingService() {
    private val TAG = "FCMConfig"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserStore.saveFCMToken(this, token)

        //create user chat notification channel
        createNotificationChannel(
            NotificationConstants.USER_CHAT_CHANNEL_ID,
            NotificationConstants.USER_CHAT_CHANNEL_NAME,
            NotificationConstants.USER_CHAT_CHANNEL_DESCRIPTION
        )

        //create group chat notification channel
        createNotificationChannel(
            NotificationConstants.GROUP_CHAT_CHANNEL_ID,
            NotificationConstants.GROUP_CHAT_CHANNEL_NAME,
            NotificationConstants.GROUP_CHAT_CHANNEL_DESCRIPTION
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val payload = JSONObject(message.notification?.body!!)
        Log.d(TAG, "onMessageReceived: $payload")

        val chatType = ChatType.valueOf(payload.getString("chatType"))
        val messageType = ChatMessageType.valueOf(payload.getString("messageType"))

        notifyByChatType(chatType, payload, messageType)
    }

    private fun notifyByChatType(
        chatType: ChatType,
        payload: JSONObject,
        messageType: ChatMessageType
    ) {
        when (chatType) {
            ChatType.USER -> {
                //notify user chat
            }

            ChatType.GROUP -> {
                //notify group chat
            }
        }
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String,
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}