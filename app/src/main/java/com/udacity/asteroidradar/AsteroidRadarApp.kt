package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.worker.DeleteAsteroidsWorker
import com.udacity.asteroidradar.worker.RefreshAsteroidsWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidRadarApp: Application() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        scope.launch {
            configureRefreshWorker()
            configureDeleteWorker()
        }
    }

    private fun configureRefreshWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<RefreshAsteroidsWorker>(
            1
            , TimeUnit.DAYS
        ).setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshAsteroidsWorker.WORK_NAME
            , ExistingPeriodicWorkPolicy.KEEP
            , periodicWorkRequest
        )
    }

    private fun configureDeleteWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DeleteAsteroidsWorker>(
            1
            , TimeUnit.DAYS
        ).setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            DeleteAsteroidsWorker.WORK_NAME
            , ExistingPeriodicWorkPolicy.KEEP
            , periodicWorkRequest
        )
    }
}