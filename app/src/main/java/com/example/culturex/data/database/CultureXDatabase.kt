package com.example.culturex.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.culturex.data.converters.StringListConverter
import com.example.culturex.data.dao.CachedContentDao
import com.example.culturex.data.dao.PendingSyncDao
import com.example.culturex.data.entities.CachedContent
import com.example.culturex.data.entities.PendingSyncOperation

/**
 * Room Database for offline content caching and sync operations
 */
@Database(
    entities = [
        CachedContent::class,
        PendingSyncOperation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class CultureXDatabase : RoomDatabase(){

    abstract fun cachedContentDao(): CachedContentDao
    abstract fun pendingSyncDao(): PendingSyncDao

    companion object {
        @Volatile
        private var INSTANCE: CultureXDatabase? = null

        fun getDatabase(context: Context): CultureXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CultureXDatabase::class.java,
                    "culturex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}