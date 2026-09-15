package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface YouTubeDao {

    // Offline Downloads
    @Query("SELECT * FROM offline_downloads ORDER BY downloadedAt DESC")
    fun getAllOfflineVideos(): Flow<List<OfflineVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineVideo(video: OfflineVideoEntity)

    @Query("DELETE FROM offline_downloads WHERE videoId = :videoId")
    suspend fun deleteOfflineVideo(videoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM offline_downloads WHERE videoId = :videoId)")
    suspend fun isVideoDownloaded(videoId: String): Boolean

    // Watch History
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE videoId = :videoId")
    suspend fun deleteWatchHistoryItem(videoId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    // Watch Later
    @Query("SELECT * FROM watch_later ORDER BY addedTimestamp DESC")
    fun getWatchLaterVideos(): Flow<List<WatchLaterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchLater(item: WatchLaterEntity)

    @Query("DELETE FROM watch_later WHERE videoId = :videoId")
    suspend fun deleteWatchLater(videoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watch_later WHERE videoId = :videoId)")
    suspend fun isWatchLater(videoId: String): Boolean

    // Playlists
    @Query("SELECT * FROM custom_playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<CustomPlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: CustomPlaylistEntity)

    @Query("DELETE FROM custom_playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    // Playlist Items
    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    fun getItemsForPlaylist(playlistId: String): Flow<List<PlaylistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(item: PlaylistItemEntity)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun deletePlaylistItem(playlistId: String, videoId: String)
}
