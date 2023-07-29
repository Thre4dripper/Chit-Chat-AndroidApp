package com.example.chitchatapp.notifications

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.NotificationConstants
import org.json.JSONObject

class UserChatNotification {
    companion object {
        private val TAG = "UserChatNotification"
        fun textNotification(context: Context, payload: JSONObject) {

            val notifierName = payload.getString("notifierName")
            val notifierId = payload.getString("notifierId")
            val notifierImage = payload.getString("notifierImage")
            val textMessage = payload.getString("message")

            val notificationHash = FCMConfig.stringToUniqueHash(notifierId)
            val notificationId =
                ("${NotificationConstants.USER_TEXT_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationUserImage = FCMConfig.getBitmapFromUrl(notifierImage)
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.USER_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(notifierName)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentText(textMessage)
                    .setLargeIcon(notificationUserImage)
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText(textMessage)
                    ).setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(notificationId, builder)
                } catch (e: SecurityException) {
                    Log.d(TAG, "e: $e")
                }
            }
        }
    }
}