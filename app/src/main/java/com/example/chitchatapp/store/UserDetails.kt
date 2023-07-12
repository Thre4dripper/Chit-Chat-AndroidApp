package com.example.chitchatapp.store

import android.content.Context

class UserDetails {
    companion object {
        private const val PREF_INSTANCE = "user_details"
        private const val PREF_USERNAME = "username"
        private const val PREF_NAME = "name"
        private const val PREF_BIO = "bio"

        fun saveUsername(context: Context, username: String) {
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

        fun saveName(context: Context, name: String) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_NAME, name)
            editor.apply()
        }

        fun getName(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PREF_NAME, null)
        }

        fun saveBio(context: Context, bio: String) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(PREF_BIO, bio)
            editor.apply()
        }

        fun getBio(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences(PREF_INSTANCE, Context.MODE_PRIVATE)
            return sharedPreferences.getString(PREF_BIO, null)
        }
    }
}