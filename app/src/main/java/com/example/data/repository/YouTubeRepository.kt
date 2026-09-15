package com.example.data.repository

import com.example.data.SampleData
import com.example.data.local.CustomPlaylistEntity
import com.example.data.local.OfflineVideoEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchLaterEntity
import com.example.data.local.YouTubeDao
import com.example.model.CommentItem
import com.example.model.CommunityPost
import com.example.model.GoogleAccount
import com.example.model.LiveChatMessage
import com.example.model.ShortItem
import com.example.model.VideoItem
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.AppThemePresets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class BellNotificationMode {
    ALL, PERSONALIZED, NONE
}

class YouTubeRepository(private val dao: YouTubeDao) {

    private val _videos = MutableStateFlow<List<VideoItem>>(SampleData.sampleVideos)
    val videos: Flow<List<VideoItem>> = _videos.asStateFlow()

    private val _shorts = MutableStateFlow<List<ShortItem>>(SampleData.sampleShorts)
    val shorts: Flow<List<ShortItem>> = _shorts.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(SampleData.sampleCommunityPosts)
    val communityPosts: Flow<List<CommunityPost>> = _communityPosts.asStateFlow()

    private val _currentAccount = MutableStateFlow<GoogleAccount?>(null)
    val currentAccount = _currentAccount.asStateFlow()

    private val _subscribedChannels = MutableStateFlow<Map<String, BellNotificationMode>>(emptyMap())
    val subscribedChannels = _subscribedChannels.asStateFlow()

    private val _likedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val likedVideoIds = _likedVideoIds.asStateFlow()

    private val _dislikedVideoIds = MutableStateFlow<Set<String>>(emptySet())
    val dislikedVideoIds = _dislikedVideoIds.asStateFlow()

    private val _isAmbientModeEnabled = MutableStateFlow(true)
    val isAmbientModeEnabled = _isAmbientModeEnabled.asStateFlow()

    private val _isBackgroundPlaybackEnabled = MutableStateFlow(true)
    val isBackgroundPlaybackEnabled = _isBackgroundPlaybackEnabled.asStateFlow()

    private val _isRestrictedModeEnabled = MutableStateFlow(false)
    val isRestrictedModeEnabled = _isRestrictedModeEnabled.asStateFlow()

    private val _isAdFreePremiumEnabled = MutableStateFlow(true)
    val isAdFreePremiumEnabled = _isAdFreePremiumEnabled.asStateFlow()

    private val _currentTheme = MutableStateFlow<AppThemePreset>(AppThemePresets.GreenBlack)
    val currentTheme = _currentTheme.asStateFlow()

    fun setTheme(preset: AppThemePreset) {
        _currentTheme.value = preset
    }

    private val _joinedMemberships = MutableStateFlow<Set<String>>(emptySet())
    val joinedMemberships = _joinedMemberships.asStateFlow()

    // Room DB streams
    val offlineVideos: Flow<List<OfflineVideoEntity>> = dao.getAllOfflineVideos()
    val watchHistory: Flow<List<WatchHistoryEntity>> = dao.getWatchHistory()
    val watchLater: Flow<List<WatchLaterEntity>> = dao.getWatchLaterVideos()
    val playlists: Flow<List<CustomPlaylistEntity>> = dao.getAllPlaylists()

    // Google Sign-In Actions
    fun signInWithGoogle(account: GoogleAccount) {
        _currentAccount.value = account
    }

    fun signOut() {
        _currentAccount.value = null
    }

    suspend fun searchOnlineVideos(query: String): List<VideoItem> {
        val online = com.example.data.network.YouTubeApiService.searchVideos(query)
        if (online.isNotEmpty()) {
            // Also append discovered videos to repository's local cache so they can be referenced
            val current = _videos.value
            val merged = (online + current).distinctBy { it.id }
            _videos.value = merged
            return online
        }
        return _videos.value.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.channelName.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }

    suspend fun loadCategoryVideos(category: String): List<VideoItem> {
        val online = com.example.data.network.YouTubeApiService.fetchCategoryVideos(category)
        if (online.isNotEmpty()) {
            val current = _videos.value
            val merged = (online + current).distinctBy { it.id }
            _videos.value = merged
            return online
        }
        return _videos.value.filter {
            if (category.equals("All", ignoreCase = true)) true
            else it.category.contains(category, ignoreCase = true)
        }
    }

    suspend fun loadTrendingFeed() {
        try {
            val trending = com.example.data.network.YouTubeApiService.fetchCategoryVideos("all")
            if (trending.isNotEmpty()) {
                val current = _videos.value
                val merged = (trending + current).distinctBy { it.id }
                _videos.value = merged
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Likes & Dislikes
    fun toggleLike(videoId: String) {
        val current = _likedVideoIds.value
        if (current.contains(videoId)) {
            _likedVideoIds.value = current - videoId
        } else {
            _likedVideoIds.value = current + videoId
            _dislikedVideoIds.value = _dislikedVideoIds.value - videoId
        }
    }

    fun toggleDislike(videoId: String) {
        val current = _dislikedVideoIds.value
        if (current.contains(videoId)) {
            _dislikedVideoIds.value = current - videoId
        } else {
            _dislikedVideoIds.value = current + videoId
            _likedVideoIds.value = _likedVideoIds.value - videoId
        }
    }

    // Subscription & Bell notification
    fun toggleSubscription(channelId: String) {
        val map = _subscribedChannels.value.toMutableMap()
        if (map.containsKey(channelId)) {
            map.remove(channelId)
        } else {
            map[channelId] = BellNotificationMode.ALL
        }
        _subscribedChannels.value = map
    }

    fun setBellNotification(channelId: String, mode: BellNotificationMode) {
        val map = _subscribedChannels.value.toMutableMap()
        map[channelId] = mode
        _subscribedChannels.value = map
    }

    fun toggleMembership(channelId: String) {
        val current = _joinedMemberships.value
        _joinedMemberships.value = if (current.contains(channelId)) {
            current - channelId
        } else {
            current + channelId
        }
    }

    // Offline downloads
    suspend fun downloadVideo(video: VideoItem, quality: String = "1080p") {
        dao.insertOfflineVideo(
            OfflineVideoEntity(
                videoId = video.id,
                title = video.title,
                channelName = video.channelName,
                thumbnailUrl = video.thumbnailUrl,
                durationSeconds = video.durationSeconds,
                quality = quality,
                fileSizeMb = 48.2f,
                isEncrypted = true
            )
        )
    }

    suspend fun removeOfflineVideo(videoId: String) {
        dao.deleteOfflineVideo(videoId)
    }

    suspend fun isVideoDownloaded(videoId: String): Boolean {
        return dao.isVideoDownloaded(videoId)
    }

    // Watch History
    suspend fun addToHistory(video: VideoItem, positionSeconds: Int) {
        dao.insertWatchHistory(
            WatchHistoryEntity(
                videoId = video.id,
                title = video.title,
                channelName = video.channelName,
                thumbnailUrl = video.thumbnailUrl,
                durationSeconds = video.durationSeconds,
                watchedPositionSeconds = positionSeconds,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() {
        dao.clearWatchHistory()
    }

    // Watch Later
    suspend fun toggleWatchLater(video: VideoItem) {
        if (dao.isWatchLater(video.id)) {
            dao.deleteWatchLater(video.id)
        } else {
            dao.insertWatchLater(
                WatchLaterEntity(
                    videoId = video.id,
                    title = video.title,
                    channelName = video.channelName,
                    thumbnailUrl = video.thumbnailUrl,
                    durationSeconds = video.durationSeconds
                )
            )
        }
    }

    suspend fun isWatchLater(videoId: String): Boolean = dao.isWatchLater(videoId)

    // Playlists
    suspend fun createPlaylist(title: String, description: String, privacy: String = "Public") {
        dao.insertPlaylist(
            CustomPlaylistEntity(
                id = "pl_${UUID.randomUUID().toString().take(8)}",
                title = title,
                description = description,
                privacy = privacy
            )
        )
    }

    // Creator Studio Upload Simulator
    fun uploadVideo(
        title: String,
        description: String,
        category: String,
        visibility: String,
        thumbnailUrl: String
    ) {
        val newVideo = VideoItem(
            id = "vid_${System.currentTimeMillis()}",
            title = title,
            description = description,
            channelId = "user_channel",
            channelName = _currentAccount.value?.displayName ?: "Your Channel",
            channelAvatarUrl = _currentAccount.value?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
            subscriberCount = "1.2K",
            thumbnailUrl = thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=800&auto=format&fit=crop" },
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            durationSeconds = 320,
            viewCount = 1,
            uploadDateText = "Just now",
            likesCount = 0,
            dislikesCount = 0,
            category = category
        )
        _videos.value = listOf(newVideo) + _videos.value
    }

    // Settings
    fun toggleAmbientMode() {
        _isAmbientModeEnabled.value = !_isAmbientModeEnabled.value
    }

    fun toggleBackgroundPlayback() {
        _isBackgroundPlaybackEnabled.value = !_isBackgroundPlaybackEnabled.value
    }

    fun toggleRestrictedMode() {
        _isRestrictedModeEnabled.value = !_isRestrictedModeEnabled.value
    }
}
