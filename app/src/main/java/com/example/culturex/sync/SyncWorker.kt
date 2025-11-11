package com.example.culturex.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker for background synchronization
 * This worker runs periodically to sync offline changes when device is online
 */

class SyncWorker(context: Context,
                 workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        const val WORK_NAME = "CultureXSyncWork"

        /**
         * Schedule periodic sync work
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES  // Minimum interval is 15 minutes
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )

            Log.d(TAG, "Periodic sync work scheduled")
        }

        /**
         * Trigger one-time sync immediately
         */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "OneTimeSync",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )

            Log.d(TAG, "One-time sync work enqueued")
        }

        /**
         * Cancel all sync work
         */
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Sync work cancelled")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting background sync")

        return@withContext try {
            val syncManager = SyncManager.getInstance(applicationContext)
            val networkStateManager = NetworkStateManager.getInstance(applicationContext)

            // Check if network is available
            if (!networkStateManager.isCurrentlyConnected()) {
                Log.d(TAG, "No network connection, skipping sync")
                return@withContext Result.retry()
            }

            // Perform sync
            val result = syncManager.syncAllPendingOperations()

            if (result.success) {
                Log.d(TAG, "Background sync completed successfully: ${result.syncedCount} operations synced")
                Result.success(
                    workDataOf(
                        "synced_count" to result.syncedCount,
                        "message" to result.message
                    )
                )
            } else {
                Log.w(TAG, "Background sync completed with failures: ${result.failedCount} operations failed")
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure(
                        workDataOf(
                            "failed_count" to result.failedCount,
                            "message" to result.message
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during background sync: ${e.message}", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(
                    workDataOf("error" to (e.message ?: "Unknown error"))
                )
            }
        }
    }
}