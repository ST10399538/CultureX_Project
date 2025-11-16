package com.example.culturex.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.culturex.data.entities.CachedContent

/**
 * Data Access Object for cached content
 */
@Dao
interface CachedContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: CachedContent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contentList: List<CachedContent>)

    @Update
    suspend fun updateContent(content: CachedContent)

    @Delete
    suspend fun deleteContent(content: CachedContent)

    @Query("SELECT * FROM cached_content WHERE id = :contentId")
    suspend fun getContentById(contentId: String): CachedContent?

    @Query("SELECT * FROM cached_content WHERE id = :contentId")
    fun getContentByIdLiveData(contentId: String): LiveData<CachedContent?>

    @Query("SELECT * FROM cached_content WHERE countryId = :countryId AND categoryId = :categoryId")
    suspend fun getContent(countryId: String, categoryId: String): CachedContent?

    @Query("SELECT * FROM cached_content WHERE countryId = :countryId AND categoryId = :categoryId")
    fun getContentLiveData(countryId: String, categoryId: String): LiveData<CachedContent?>

    @Query("SELECT * FROM cached_content WHERE isBookmarked = 1")
    fun getBookmarkedContent(): LiveData<List<CachedContent>>

    @Query("SELECT * FROM cached_content WHERE isSavedForOffline = 1")
    fun getSavedForOfflineContent(): LiveData<List<CachedContent>>

    @Query("SELECT * FROM cached_content WHERE isSynced = 0")
    suspend fun getUnsyncedContent(): List<CachedContent>

    @Query("UPDATE cached_content SET isBookmarked = :bookmarked WHERE id = :contentId")
    suspend fun updateBookmarkStatus(contentId: String, bookmarked: Boolean)

    @Query("UPDATE cached_content SET isSavedForOffline = :saved WHERE id = :contentId")
    suspend fun updateSaveOfflineStatus(contentId: String, saved: Boolean)

    @Query("UPDATE cached_content SET isSynced = :synced WHERE id = :contentId")
    suspend fun updateSyncStatus(contentId: String, synced: Boolean)

    @Query("SELECT COUNT(*) FROM cached_content WHERE isSavedForOffline = 1")
    fun getSavedContentCount(): LiveData<Int>

    @Query("DELETE FROM cached_content WHERE isSavedForOffline = 0 AND isBookmarked = 0 AND lastUpdated < :timestamp")
    suspend fun deleteOldUnmarkedContent(timestamp: Long)

    @Query("SELECT * FROM cached_content")
    fun getAllContent(): LiveData<List<CachedContent>>
}