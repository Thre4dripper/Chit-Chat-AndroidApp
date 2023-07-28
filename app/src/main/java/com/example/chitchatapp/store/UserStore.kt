package com.example.chitchatapp.store

import android.content.Context

class UserStore {
    companion object {
        private const val PREF_INSTANCE = "user_details"
        private const val PREF_USERNAME = "username"
        private const val PREF_FCM_TOKEN = "fcm_token"

        fun saveUsername(context: Context, username: String?) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_USERNAME, username)
            editor.apply()
        }

        fun getUsername(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PREF_USERNAME, null)
        }

        fun saveFCMToken(context: Context, token: String) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_FCM_TOKEN, token)
            editor.apply()
        }

        fun getFCMToken(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PREF_FCM_TOKEN, null)
        }
    }
}