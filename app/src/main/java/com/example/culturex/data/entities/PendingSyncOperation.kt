package com.example.culturex.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to track actions performed offline that need to be synced
 */
@Entity(tableName = "pending_sync")

data class PendingSyncOperation(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val operationType: SyncOperationType,
    val contentId: String,
    val timestamp: Long = System.currentTimeMillis(),

    // Additional data based on operation type
    val bookmarked: Boolean? = null,
    val savedOffline: Boolean? = null,

    val retryCount: Int = 0,
    val lastAttempt: Long? = null,
    val errorMessage: String? = null
)

enum class SyncOperationType {
    BOOKMARK_ADD,
    BOOKMARK_REMOVE,
    SAVE_OFFLINE_ADD,
    SAVE_OFFLINE_REMOVE,
    CONTENT_UPDATE
}
