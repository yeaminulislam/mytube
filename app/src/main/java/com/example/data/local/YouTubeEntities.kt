package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_downloads")
data class OfflineVideoEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val quality: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSizeMb: Float = 45.5f,
    val isEncrypted: Boolean = true
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val watchedPositionSeconds: Int,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_later")
data class WatchLaterEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_playlists")
data class CustomPlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val privacy: String, // "Public", "Unlisted", "Private"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_items")
data class PlaylistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: String,
    val videoId: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val addedAt: Long = System.currentTimeMillis()
)
