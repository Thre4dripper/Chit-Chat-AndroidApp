package com.example.chitchatapp.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.ChatConstants
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.ui.activities.ChatActivity
import org.json.JSONObject

class UserChatNotifications {
    companion object {
        private val TAG = "UserChatNotification"
        fun textNotification(context: Context, payload: JSONObject) {

            val notifierName = payload.getString(NotificationConstants.NOTIFIER_NAME)
            val notifierId = payload.getString(NotificationConstants.NOTIFIER_ID)
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val chatId = payload.getString(ChatConstants.CHAT_ID)
            val text = payload.getString(NotificationConstants.NOTIFICATION_TEXT)

            val notificationHash = FCMConfig.stringToUniqueHash(notifierId)
            val notificationId =
                ("${NotificationConstants.USER_TEXT_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationUserImage = FCMConfig.getBitmapFromUrl(notifierImage)

            val intent = Intent(context, ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(ChatConstants.CHAT_ID, chatId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.USER_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(notifierName)
                    .setPriority(NotificationCompat.PRIORITY_MAX).setContentText(text)
                    .setLargeIcon(notificationUserImage).setStyle(
                        NotificationCompat.BigTextStyle().setBigContentTitle(notifierName)
                            .bigText(text)
                    ).setContentIntent(pendingIntent).setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(notificationId, builder)
                } catch (e: SecurityException) {
                    Log.d(TAG, "e: $e")
                }
            }
        }

        fun imageNotification(context: Context, payload: JSONObject) {
            val notifierName = payload.getString(NotificationConstants.NOTIFIER_NAME)
            val notifierId = payload.getString(NotificationConstants.NOTIFIER_ID)
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val chatId = payload.getString(ChatConstants.CHAT_ID)
            val image = payload.getString(NotificationConstants.NOTIFICATION_IMAGE)

            val notificationHash = FCMConfig.stringToUniqueHash(notifierId)
            val notificationId =
                ("${NotificationConstants.USER_IMAGE_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationUserImage = FCMConfig.getBitmapFromUrl(notifierImage)
            val notificationImage = FCMConfig.getBitmapFromUrl(image)

            val intent = Intent(context, ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(ChatConstants.CHAT_ID, chatId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )

            val builder =
                NotificationCompat.Builder(context, NotificationConstants.USER_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(notifierName)
                    .setContentText("\uD83D\uDCF7 Image")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationUserImage).setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(notificationImage)
                    )
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build()

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(notificationId, builder)
                } catch (e: SecurityException) {
                    Log.d(TAG, "e: $e")
                }
            }
        }

        fun stickerNotification(context: Context, payload: JSONObject) {
            val notifierName = payload.getString(NotificationConstants.NOTIFIER_NAME)
            val notifierId = payload.getString(NotificationConstants.NOTIFIER_ID)
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val chatId = payload.getString(ChatConstants.CHAT_ID)

            val notificationHash = FCMConfig.stringToUniqueHash(notifierId)
            val notificationId =
                ("${NotificationConstants.USER_STICKER_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationUserImage = FCMConfig.getBitmapFromUrl(notifierImage)

            val intent = Intent(context, ChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(ChatConstants.CHAT_ID, chatId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.USER_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(notifierName)
                    .setContentText("\uD83C\uDF20 Sticker")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationUserImage)
                    .setContentIntent(pendingIntent)
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