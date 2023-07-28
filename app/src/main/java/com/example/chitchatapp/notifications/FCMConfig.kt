package com.example.chitchatapp.notifications

import android.util.Log
import com.example.chitchatapp.store.UserStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FCMConfig:FirebaseMessagingService() {
    private val TAG = "FCMConfig"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserStore.saveFCMToken(this, token)
        Log.d(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val payload = JSONObject(message.notification?.body!!)
        Log.d(TAG, "onMessageReceived: $payload")
    }
}