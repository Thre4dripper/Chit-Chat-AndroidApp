package com.example.chitchatapp

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.firebase.user.UpdateStatus
import com.example.chitchatapp.store.UserStore
import com.google.firebase.firestore.FirebaseFirestore

class MyApplication : Application(), ActivityLifecycleCallbacks {
    private val TAG = "MyApplication"

    companion object {
        var activityReferences = 0
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {
        if (++activityReferences == 1) {
            // App enters foreground
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(this)
            UpdateStatus.updateUserStatus(firestore, loggedInUser, UserStatus.Online)
        }
    }

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {
        if (--activityReferences == 0) {
            // App enters background
            val firestore = FirebaseFirestore.getInstance()
            val loggedInUser = UserStore.getUsername(this)
            UpdateStatus.updateUserStatus(firestore, loggedInUser, UserStatus.LastSeen)
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}