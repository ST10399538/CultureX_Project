package com.example.culturex.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.culturex.data.entities.PendingSyncOperation
import com.example.culturex.data.entities.SyncOperationType

/**
 * Data Access Object for pending sync operations
 */
@Dao
interface PendingSyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: PendingSyncOperation)

    @Delete
    suspend fun deleteOperation(operation: PendingSyncOperation)

    @Query("SELECT * FROM pending_sync ORDER BY timestamp ASC")
    suspend fun getAllPendingOperations(): List<PendingSyncOperation>

    @Query("SELECT * FROM pending_sync WHERE contentId = :contentId")
    suspend fun getOperationsForContent(contentId: String): List<PendingSyncOperation>

    @Query("DELETE FROM pending_sync WHERE id = :operationId")
    suspend fun deleteOperationById(operationId: Long)

    @Query("DELETE FROM pending_sync WHERE contentId = :contentId AND operationType = :type")
    suspend fun deleteOperationsByTypeAndContent(contentId: String, type: SyncOperationType)

    @Query("UPDATE pending_sync SET retryCount = :retryCount, lastAttempt = :lastAttempt, errorMessage = :error WHERE id = :operationId")
    suspend fun updateRetryInfo(operationId: Long, retryCount: Int, lastAttempt: Long, error: String?)

    @Query("SELECT COUNT(*) FROM pending_sync")
    fun getPendingOperationsCount(): LiveData<Int>

    @Query("DELETE FROM pending_sync")
    suspend fun clearAll()
}