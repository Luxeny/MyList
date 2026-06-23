package com.example.mylist

import android.app.Application
import android.content.Intent
import androidx.work.*
import com.example.mylist.analytics.AppAnalytics
import com.example.mylist.background.AppMonitorService
import com.example.mylist.background.CleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyListApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var appAnalytics: AppAnalytics

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun onCreate() {
        super.onCreate()
        appAnalytics.initialize(this)
        setupWorkManager()
        startAppMonitorService()
    }

    private fun setupWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CleanupWork",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }

    private fun startAppMonitorService() {
        startService(Intent(this, AppMonitorService::class.java))
    }
}