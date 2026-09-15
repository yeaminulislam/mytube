package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OfflineVideoEntity::class,
        WatchHistoryEntity::class,
        WatchLaterEntity::class,
        CustomPlaylistEntity::class,
        PlaylistItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class YouTubeDatabase : RoomDatabase() {
    abstract fun youTubeDao(): YouTubeDao

    companion object {
        @Volatile
        private var INSTANCE: YouTubeDatabase? = null

        fun getDatabase(context: Context): YouTubeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YouTubeDatabase::class.java,
                    "youtube_clone_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
