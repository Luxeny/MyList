package com.example.mylist.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("CleanupWorker", "Starting periodic cleanup task...")
        // Simulated cleanup logic
        return Result.success()
    }
}