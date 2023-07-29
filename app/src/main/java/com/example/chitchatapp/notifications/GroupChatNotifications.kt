package com.example.chitchatapp.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.GroupConstants
import com.example.chitchatapp.constants.NotificationConstants
import com.example.chitchatapp.ui.activities.GroupChatActivity
import org.json.JSONObject

class GroupChatNotifications {
    companion object {
        private val TAG = "GroupChatNotifications"
        fun textNotification(context: Context, payload: JSONObject) {

            val notifierName = payload.getString(NotificationConstants.NOTIFIER_NAME)
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val groupId = payload.getString(GroupConstants.GROUP_ID)
            val groupName = payload.getString(GroupConstants.GROUP_NAME)

            // notificationImage is the group image if it exists, otherwise it is the notifierImage
            val notificationImage = payload.has(GroupConstants.GROUP_IMAGE).let {
                if (it) {
                    payload.getString(GroupConstants.GROUP_IMAGE)
                } else {
                    notifierImage
                }
            }
            val text = payload.getString(NotificationConstants.TEXT)

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_TEXT_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationIconImage = FCMConfig.getBitmapFromUrl(notificationImage)

            val intent = Intent(context, GroupChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(GroupConstants.GROUP_ID, groupId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentText("$notifierName: $text")
                    .setLargeIcon(notificationIconImage)
                    .setStyle(
                        NotificationCompat.BigTextStyle().setBigContentTitle(groupName)
                            .bigText("$notifierName: $text")
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

        fun imageNotification(context: Context, payload: JSONObject) {
            val notifierName = payload.getString(NotificationConstants.NOTIFIER_NAME)
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val groupId = payload.getString(GroupConstants.GROUP_ID)
            val groupName = payload.getString(GroupConstants.GROUP_NAME)
            val notificationImage = payload.has(GroupConstants.GROUP_IMAGE).let {
                if (it) {
                    payload.getString(GroupConstants.GROUP_IMAGE)
                } else {
                    notifierImage
                }
            }
            val image = payload.getString(NotificationConstants.IMAGE)

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_IMAGE_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationIconImage = FCMConfig.getBitmapFromUrl(notificationImage)
            val notificationMessageImage = FCMConfig.getBitmapFromUrl(image)

            val intent = Intent(context, GroupChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(GroupConstants.GROUP_ID, groupId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setContentText("$notifierName: \uD83D\uDCF7 Image")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationIconImage)
                    .setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(notificationMessageImage)
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
            val notifierImage = payload.getString(NotificationConstants.NOTIFIER_IMAGE)
            val groupId = payload.getString(GroupConstants.GROUP_ID)
            val groupName = payload.getString(GroupConstants.GROUP_NAME)
            val notificationImage = payload.has(GroupConstants.GROUP_IMAGE).let {
                if (it) {
                    payload.getString(GroupConstants.GROUP_IMAGE)
                } else {
                    notifierImage
                }
            }

            val notificationHash = FCMConfig.stringToUniqueHash(groupId)
            val notificationId =
                ("${NotificationConstants.GROUP_STICKER_NOTIFICATION_ID}$notificationHash").toInt()

            val notificationIconImage = FCMConfig.getBitmapFromUrl(notificationImage)

            val intent = Intent(context, GroupChatActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            intent.putExtra(GroupConstants.GROUP_ID, groupId)

            val pendingIntent = PendingIntent.getActivity(
                context, notificationId, intent, PendingIntent.FLAG_MUTABLE
            )
            val builder =
                NotificationCompat.Builder(context, NotificationConstants.GROUP_CHAT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(groupName)
                    .setContentText("$notifierName: \uD83C\uDF20 Sticker")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLargeIcon(notificationIconImage)
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