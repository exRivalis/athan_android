package com.alterpat.athan.tool

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AthanService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }



    override fun onCreate() {
        super.onCreate()
        scheduleNotifications(this, 1, 16, 10, 57, "Test2")
    }

}