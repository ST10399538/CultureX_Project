package com.example.culturex.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.culturex.data.entities.PendingSyncOperation
import com.example.culturex.data.entities.SyncOperationType
import com.example.culturex.data.repository.OfflineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncManager(private val context: Context) {

    private val offlineRepository = OfflineRepository.getInstance(context)

    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_COUNT = 3

        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Check if device has internet connectivity
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Sync all pending operations
     */
    suspend fun syncAllPendingOperations(): SyncResult = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No network available, skipping sync")
            return@withContext SyncResult(
                success = false,
                syncedCount = 0,
                failedCount = 0,
                message = "No network connection"
            )
        }

        val pendingOperations = offlineRepository.getPendingOperations()
        Log.d(TAG, "Starting sync for ${pendingOperations.size} operations")

        var syncedCount = 0
        var failedCount = 0

        for (operation in pendingOperations) {
            try {
                val result = syncOperation(operation)
                if (result) {
                    syncedCount++
                    offlineRepository.deleteSyncOperation(operation)
                    offlineRepository.markContentAsSynced(operation.contentId)
                    Log.d(TAG, "Operation ${operation.id} synced successfully")
                } else {
                    failedCount++
                    handleSyncFailure(operation, "Sync failed")
                }
            } catch (e: Exception) {
                failedCount++
                handleSyncFailure(operation, e.message ?: "Unknown error")
                Log.e(TAG, "Error syncing operation ${operation.id}: ${e.message}", e)
            }
        }

        Log.d(TAG, "Sync completed: $syncedCount synced, $failedCount failed")

        SyncResult(
            success = failedCount == 0,
            syncedCount = syncedCount,
            failedCount = failedCount,
            message = if (failedCount == 0)
                "All operations synced successfully"
            else
                "Synced $syncedCount operations, $failedCount failed"
        )
    }

    /**
     * Sync a single operation with the server
     */
    private suspend fun syncOperation(operation: PendingSyncOperation): Boolean {
        // Simulate API call based on operation type
        // In a real implementation, this would call your API service

        return when (operation.operationType) {
            SyncOperationType.BOOKMARK_ADD -> {
                syncBookmarkOperation(operation.contentId, true)
            }
            SyncOperationType.BOOKMARK_REMOVE -> {
                syncBookmarkOperation(operation.contentId, false)
            }
            SyncOperationType.SAVE_OFFLINE_ADD -> {
                syncSaveOfflineOperation(operation.contentId, true)
            }
            SyncOperationType.SAVE_OFFLINE_REMOVE -> {
                syncSaveOfflineOperation(operation.contentId, false)
            }
            SyncOperationType.CONTENT_UPDATE -> {
                syncContentUpdate(operation.contentId)
            }
        }
    }

    /**
     * Sync bookmark operation with API
     */
    private suspend fun syncBookmarkOperation(contentId: String, isBookmarked: Boolean): Boolean {
        // TODO: Replace with actual API call
        // Example:
        // val response = apiService.updateBookmark(contentId, isBookmarked)
        // return response.isSuccessful

        Log.d(TAG, "Syncing bookmark for $contentId: $isBookmarked")

        // Simulate API call delay
        kotlinx.coroutines.delay(500)

        // Simulate success (replace with actual API logic)
        return true
    }

    /**
     * Sync save offline operation with API
     */
    private suspend fun syncSaveOfflineOperation(contentId: String, isSaved: Boolean): Boolean {
        // TODO: Replace with actual API call
        // Example:
        // val response = apiService.updateSaveOffline(contentId, isSaved)
        // return response.isSuccessful

        Log.d(TAG, "Syncing save offline for $contentId: $isSaved")

        // Simulate API call delay
        kotlinx.coroutines.delay(500)

        // Simulate success (replace with actual API logic)
        return true
    }

    /**
     * Sync content update with API
     */
    private suspend fun syncContentUpdate(contentId: String): Boolean {
        // TODO: Replace with actual API call
        // Example:
        // val response = apiService.updateContent(contentId, updatedData)
        // return response.isSuccessful

        Log.d(TAG, "Syncing content update for $contentId")

        // Simulate API call delay
        kotlinx.coroutines.delay(500)

        // Simulate success (replace with actual API logic)
        return true
    }

    /**
     * Handle sync failure with retry logic
     */
    private suspend fun handleSyncFailure(operation: PendingSyncOperation, error: String) {
        val newRetryCount = operation.retryCount + 1

        if (newRetryCount >= MAX_RETRY_COUNT) {
            Log.w(TAG, "Max retry count reached for operation ${operation.id}, deleting")
            offlineRepository.deleteSyncOperation(operation)
        } else {
            Log.d(TAG, "Updating retry count for operation ${operation.id}: $newRetryCount")
            offlineRepository.updateSyncRetry(operation.id, newRetryCount, error)
        }
    }
}

/**
 * Result of a sync operation
 */
data class SyncResult(
    val success: Boolean,
    val syncedCount: Int,
    val failedCount: Int,
    val message: String
)
