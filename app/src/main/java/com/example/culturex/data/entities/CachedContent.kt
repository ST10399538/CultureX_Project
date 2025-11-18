package com.example.culturex.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.culturex.data.converters.StringListConverter

/**
 * Entity representing cached content for offline access
 */
@Entity(tableName = "cached_content")
@TypeConverters(StringListConverter::class)

data class CachedContent(

    @PrimaryKey
    val id: String, // Format: "countryId_categoryId"

    val countryId: String,
    val categoryId: String,
    val countryName: String?,
    val categoryName: String?,
    val title: String?,
    val content: String?,

    // Lists for dress code content
    val dos: List<String>?,
    val donts: List<String>?,
    val examples: List<String>?,

    // Etiquette specific data
    val formalDescription: String?,
    val formalKeyPoints: String?,
    val businessDescription: String?,
    val businessKeyPoints: String?,
    val socialDescription: String?,
    val socialKeyPoints: String?,

    // Sync metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isBookmarked: Boolean = false,
    val isSavedForOffline: Boolean = false
)
