package com.example.chitchatapp.firebase.messaging

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.NotificationConstants
import org.json.JSONObject

class Messaging {
    companion object {
        private val TAG = "Messaging"
        fun fireNotification(context: Context, payload: JSONObject) {
            val queue = Volley.newRequestQueue(context)
            val url = NotificationConstants.FCM_URL

            val request = object : JsonObjectRequest(Method.POST, url, payload, { response ->
                Log.d(TAG, "$response")
            }, { err ->
                Log.d(TAG, "${err.message}")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "key=${context.getString(R.string.fcm_server_key)}"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        }
    }
}