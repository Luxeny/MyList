package com.example.mylist.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AppMonitorService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("AppMonitorService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppMonitorService", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AppMonitorService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}