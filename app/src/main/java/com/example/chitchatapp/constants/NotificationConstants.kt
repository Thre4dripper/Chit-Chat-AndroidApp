package com.example.chitchatapp.constants

class NotificationConstants {
    companion object {
        const val FCM_URL = "https://fcm.googleapis.com/fcm/send"

        const val USER_CHAT_CHANNEL_ID = "user_chat_channel_id"
        const val USER_CHAT_CHANNEL_NAME = "User Chat"
        const val USER_CHAT_CHANNEL_DESCRIPTION = "User Chat Notifications"

        const val GROUP_CHAT_CHANNEL_ID = "group_chat_channel_id"
        const val GROUP_CHAT_CHANNEL_NAME = "Group Chat"
        const val GROUP_CHAT_CHANNEL_DESCRIPTION = "Group Chat Notifications"

        const val USER_TEXT_NOTIFICATION_ID = 1
        const val USER_IMAGE_NOTIFICATION_ID = 2
        const val USER_STICKER_NOTIFICATION_ID = 3
        const val GROUP_TEXT_NOTIFICATION_ID = 4
        const val GROUP_IMAGE_NOTIFICATION_ID = 5
        const val GROUP_STICKER_NOTIFICATION_ID = 6

        const val NOTIFICATION_TOKEN = "to"
        const val CHAT_TYPE = "chatType"
        const val MESSAGE_TYPE = "messageType"
        const val NOTIFICATION_DATA = "data"

        const val NOTIFIER_NAME = "notifierName"
        const val NOTIFIER_ID = "notifierId"
        const val NOTIFIER_IMAGE = "notifierImage"
        const val NOTIFICATION_TEXT = "notificationText"
        const val NOTIFICATION_IMAGE = "notificationImage"

    }
}