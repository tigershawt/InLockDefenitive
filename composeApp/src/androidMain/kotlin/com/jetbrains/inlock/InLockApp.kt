package com.jetbrains.inlock

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jetbrains.inlock.di.initKoin

class InLockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        initKoin()
    }
}