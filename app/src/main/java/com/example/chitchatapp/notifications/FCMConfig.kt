package com.example.chitchatapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.enums.ChatMessageType
import com.example.chitchatapp.enums.ChatType
import com.example.chitchatapp.store.UserStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.abs

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

        val payload = JSONObject((message.data as Map<*, *>?)!!)
        Log.d(TAG, "onMessageReceived: $payload")

        val chatType = ChatType.valueOf(payload.getString("chatType"))
        val messageType = ChatMessageType.valueOf(payload.getString("messageType"))

        notifyByChatType(chatType, payload, messageType)
    }

    private fun notifyByChatType(
        chatType: ChatType, payload: JSONObject, messageType: ChatMessageType
    ) {
        when (chatType.name) {
            ChatType.USER.name -> {
                when (messageType) {
                    ChatMessageType.TypeText -> {
                        UserChatNotifications.textNotification(this, payload)
                    }

                    ChatMessageType.TypeImage -> {
                        UserChatNotifications.imageNotification(this, payload)
                    }

                    ChatMessageType.TypeSticker -> {
                        UserChatNotifications.stickerNotification(this, payload)
                    }

                    else -> {
                        Log.d(TAG, "notifyByChatType: unknown message type")
                    }
                }
            }

            ChatType.GROUP.name -> {
                when (messageType) {
                    ChatMessageType.TypeText -> {
                        GroupChatNotifications.textNotification(this, payload)
                    }

                    ChatMessageType.TypeImage -> {
                        GroupChatNotifications.imageNotification(this, payload)
                    }

                    ChatMessageType.TypeSticker -> {
                        GroupChatNotifications.stickerNotification(this, payload)
                    }

                    else -> {
                        Log.d(TAG, "notifyByChatType: unknown message type")
                    }
                }
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

    companion object {
        fun getBitmapFromUrl(imageUrl: String): Bitmap? {
            return try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun stringToUniqueHash(string: String): Int {
            var hash = 0
            for (i in string.indices) {
                hash = string[i].code + (hash shl 6) + (hash shl 16) - hash
            }
            return abs(hash).toString().substring(2).toInt()
        }
    }
}