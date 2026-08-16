package com.tensormind.feelio.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tensormind.feelio.data.GroqRepository
import com.tensormind.feelio.data.UserPreferencesRepository
import com.tensormind.feelio.util.NotificationHelper
import kotlinx.coroutines.flow.first

class HydrationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userPrefs = UserPreferencesRepository(applicationContext)
        val userData = userPrefs.userData.first()
        
        val reminder = GroqRepository.getHydrationReminder(userData.name)
        
        NotificationHelper.showNotification(
            applicationContext,
            "Bask Ai Reminder 💧",
            reminder
        )
        
        return Result.success()
    }
}
