package com.example.culturex.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.culturex.data.dao.CachedContentDao
import com.example.culturex.data.dao.PendingSyncDao
import com.example.culturex.data.database.CultureXDatabase
import com.example.culturex.data.entities.CachedContent
import com.example.culturex.data.entities.PendingSyncOperation
import com.example.culturex.data.entities.SyncOperationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class OfflineRepository(context: Context) {

    private val database = CultureXDatabase.getDatabase(context)
    private val cachedContentDao: CachedContentDao = database.cachedContentDao()
    private val pendingSyncDao: PendingSyncDao = database.pendingSyncDao()

    companion object {
        private const val TAG = "OfflineRepository"
        private const val CACHE_EXPIRY_DAYS = 7L

        @Volatile
        private var INSTANCE: OfflineRepository? = null

        fun getInstance(context: Context): OfflineRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = OfflineRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    // ===== Content Caching Operations =====

    /**
     * Save content to local cache
     */
    suspend fun cacheContent(content: CachedContent) = withContext(Dispatchers.IO) {
        try {
            cachedContentDao.insertContent(content)
            Log.d(TAG, "Content cached successfully: ${content.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error caching content: ${e.message}", e)
            throw e
        }
    }

    /**
     * Get cached content by country and category
     */
    suspend fun getCachedContent(countryId: String, categoryId: String): CachedContent? =
        withContext(Dispatchers.IO) {
            try {
                cachedContentDao.getContent(countryId, categoryId)
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving cached content: ${e.message}", e)
                null
            }
        }

    /**
     * Get cached content as LiveData
     */
    fun getCachedContentLiveData(countryId: String, categoryId: String): LiveData<CachedContent?> {
        return cachedContentDao.getContentLiveData(countryId, categoryId)
    }

    /**
     * Get all bookmarked content
     */
    fun getBookmarkedContent(): LiveData<List<CachedContent>> {
        return cachedContentDao.getBookmarkedContent()
    }

    /**
     * Get all saved offline content
     */
    fun getSavedForOfflineContent(): LiveData<List<CachedContent>> {
        return cachedContentDao.getSavedForOfflineContent()
    }

    /**
     * Get count of saved content
     */
    fun getSavedContentCount(): LiveData<Int> {
        return cachedContentDao.getSavedContentCount()
    }

    // ===== Bookmark Operations =====

    /**
     * Toggle bookmark status for content
     */
    suspend fun toggleBookmark(contentId: String, isBookmarked: Boolean) =
        withContext(Dispatchers.IO) {
            try {
                cachedContentDao.updateBookmarkStatus(contentId, isBookmarked)

                // Queue sync operation
                val operation = PendingSyncOperation(
                    operationType = if (isBookmarked)
                        SyncOperationType.BOOKMARK_ADD
                    else
                        SyncOperationType.BOOKMARK_REMOVE,
                    contentId = contentId,
                    bookmarked = isBookmarked
                )
                pendingSyncDao.insertOperation(operation)

                Log.d(TAG, "Bookmark toggled for $contentId: $isBookmarked")
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling bookmark: ${e.message}", e)
                throw e
            }
        }

    // ===== Save Offline Operations =====

    /**
     * Toggle save offline status for content
     */
    suspend fun toggleSaveOffline(contentId: String, isSaved: Boolean) =
        withContext(Dispatchers.IO) {
            try {
                cachedContentDao.updateSaveOfflineStatus(contentId, isSaved)

                // Queue sync operation
                val operation = PendingSyncOperation(
                    operationType = if (isSaved)
                        SyncOperationType.SAVE_OFFLINE_ADD
                    else
                        SyncOperationType.SAVE_OFFLINE_REMOVE,
                    contentId = contentId,
                    savedOffline = isSaved
                )
                pendingSyncDao.insertOperation(operation)

                Log.d(TAG, "Save offline toggled for $contentId: $isSaved")
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling save offline: ${e.message}", e)
                throw e
            }
        }

    // ===== Sync Operations =====

    /**
     * Get all pending sync operations
     */
    suspend fun getPendingOperations(): List<PendingSyncOperation> =
        withContext(Dispatchers.IO) {
            try {
                pendingSyncDao.getAllPendingOperations()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting pending operations: ${e.message}", e)
                emptyList()
            }
        }

    /**
     * Get count of pending operations
     */
    fun getPendingOperationsCount(): LiveData<Int> {
        return pendingSyncDao.getPendingOperationsCount()
    }

    /**
     * Delete a sync operation after successful sync
     */
    suspend fun deleteSyncOperation(operation: PendingSyncOperation) =
        withContext(Dispatchers.IO) {
            try {
                pendingSyncDao.deleteOperation(operation)
                Log.d(TAG, "Sync operation deleted: ${operation.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting sync operation: ${e.message}", e)
            }
        }

    /**
     * Update retry information for failed sync
     */
    suspend fun updateSyncRetry(
        operationId: Long,
        retryCount: Int,
        error: String?
    ) = withContext(Dispatchers.IO) {
        try {
            pendingSyncDao.updateRetryInfo(
                operationId,
                retryCount,
                System.currentTimeMillis(),
                error
            )
            Log.d(TAG, "Sync retry updated for operation: $operationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sync retry: ${e.message}", e)
        }
    }

    /**
     * Mark content as synced
     */
    suspend fun markContentAsSynced(contentId: String) = withContext(Dispatchers.IO) {
        try {
            cachedContentDao.updateSyncStatus(contentId, true)
            Log.d(TAG, "Content marked as synced: $contentId")
        } catch (e: Exception) {
            Log.e(TAG, "Error marking content as synced: ${e.message}", e)
        }
    }

    // ===== Cache Management =====

    /**
     * Clean old cache entries (except bookmarked/saved)
     */
    suspend fun cleanOldCache() = withContext(Dispatchers.IO) {
        try {
            val expiryTimestamp = System.currentTimeMillis() -
                    TimeUnit.DAYS.toMillis(CACHE_EXPIRY_DAYS)
            cachedContentDao.deleteOldUnmarkedContent(expiryTimestamp)
            Log.d(TAG, "Old cache cleaned")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning old cache: ${e.message}", e)
        }
    }

    /**
     * Get all cached content
     */
    fun getAllCachedContent(): LiveData<List<CachedContent>> {
        return cachedContentDao.getAllContent()
    }
}