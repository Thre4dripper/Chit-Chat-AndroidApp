package com.example.chitchatapp.notifications

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.NotificationConstants
import org.json.JSONObject

class GroupChatNotifications {
    companion object {
        private val TAG = "GroupChatNotifications"
        fun textNotification(context: Context, payload: JSONObject) {

            val notifierName = payload.getString("notifierName")
            val notifierImage = payload.getString("notifierImage")
            val groupId = payload.getString("groupId")
            val groupName = payload.getString("groupName")
            val text = payload.getString("text")

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_TEXT_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationGroupImage = FCMConfig.getBitmapFromUrl(notifierImage)
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentText("$notifierName: $text")
                    .setLargeIcon(notificationGroupImage)
                    .setStyle(
                        NotificationCompat.BigTextStyle().setBigContentTitle(groupName)
                            .bigText("$notifierName: $text")
                    ).setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(notificationId, builder)
                } catch (e: SecurityException) {
                    Log.d(TAG, "e: $e")
                }
            }
        }

        fun imageNotification(context: Context, payload: JSONObject) {
            val notifierName = payload.getString("notifierName")
            val notifierImage = payload.getString("notifierImage")
            val groupId = payload.getString("groupId")
            val groupName = payload.getString("groupName")
            val image = payload.getString("image")

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_IMAGE_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationGroupImage = FCMConfig.getBitmapFromUrl(notifierImage)
            val notificationImage = FCMConfig.getBitmapFromUrl(image)
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setContentText("$notifierName: \uD83D\uDCF7 Image")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationGroupImage)
                    .setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(notificationImage)
                    ).setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(notificationId, builder)
                } catch (e: SecurityException) {
                    Log.d(TAG, "e: $e")
                }
            }
        }

        fun stickerNotification(context: Context, payload: JSONObject) {
            val notifierName = payload.getString("notifierName")
            val notifierImage = payload.getString("notifierImage")
            val groupId = payload.getString("groupId")
            val groupName = payload.getString("groupName")

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_STICKER_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationGroupImage = FCMConfig.getBitmapFromUrl(notifierImage)
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setContentText("$notifierName: \uD83C\uDF20 Sticker")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationGroupImage)
                    .setAutoCancel(true).build()

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