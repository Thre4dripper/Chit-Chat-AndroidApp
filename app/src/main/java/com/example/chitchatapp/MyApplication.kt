package com.example.chitchatapp

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.example.chitchatapp.enums.UserStatus
import com.example.chitchatapp.repository.UserRepository

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
            UserRepository.updateStatus(this, UserStatus.Online)
            UserRepository.updateToken(this)
        }
    }

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {
        if (--activityReferences == 0) {
            // App enters background
            UserRepository.updateStatus(this, UserStatus.LastSeen)
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}