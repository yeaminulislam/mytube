package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.repository.BellNotificationMode
import com.example.data.repository.YouTubeRepository
import com.example.model.VideoItem
import com.example.player.PlayerState
import com.example.player.VideoPlayerController
import com.example.player.VideoPlayerView
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.StudioDashboardDialog
import com.example.ui.components.ThemeSelectionDialog
import com.example.ui.components.UploadVideoDialog
import com.example.ui.components.VideoDetailView
import com.example.ui.components.WatchTimeStatsDialog
import com.example.ui.components.YouTubeTopAppBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.YouScreen
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.AppThemePresets
import com.example.ui.theme.LocalAppTheme
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, SHORTS, ADD, SUBSCRIPTIONS, YOU
}

@Composable
fun YouTubeApp(
    repository: YouTubeRepository,
    playerController: VideoPlayerController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Repository Flows
    val videos by repository.videos.collectAsState(initial = emptyList())
    val shorts by repository.shorts.collectAsState(initial = emptyList())
    val currentAccount by repository.currentAccount.collectAsState()
    val subscribedMap by repository.subscribedChannels.collectAsState()
    val likedVideoIds by repository.likedVideoIds.collectAsState()
    val dislikedVideoIds by repository.dislikedVideoIds.collectAsState()
    val isAmbientModeRepo by repository.isAmbientModeEnabled.collectAsState()
    val isBackgroundPlayback by repository.isBackgroundPlaybackEnabled.collectAsState()
    val isRestrictedMode by repository.isRestrictedModeEnabled.collectAsState()
    val currentTheme by repository.currentTheme.collectAsState()

    // Room DB Flows
    val historyVideos by repository.watchHistory.collectAsState(initial = emptyList())
    val offlineVideos by repository.offlineVideos.collectAsState(initial = emptyList())
    val watchLaterList by repository.watchLater.collectAsState(initial = emptyList())
    val customPlaylists by repository.playlists.collectAsState(initial = emptyList())

    // Player State
    val playerState by playerController.playerState.collectAsState()

    // Navigation & UI Dialog States
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var isSearchActive by remember { mutableStateOf(false) }
    var showGoogleSignInDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showWatchTimeDialog by remember { mutableStateOf(false) }
    var showStudioDialog by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // When full player is visible vs minimized
    var isPlayerMinimized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.loadTrendingFeed()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = currentTheme.background,
        topBar = {
            if (!isSearchActive && currentTab != MainTab.SHORTS && (playerState.currentVideo == null || isPlayerMinimized)) {
                YouTubeTopAppBar(
                    currentAccount = currentAccount,
                    onSearchClick = { isSearchActive = true },
                    onNotificationsClick = {
                        Toast.makeText(context, "3 new notifications from subscribed channels", Toast.LENGTH_SHORT).show()
                    },
                    onCastClick = {
                        Toast.makeText(context, "Searching for Cast devices (Chromecast / Smart TV)...", Toast.LENGTH_SHORT).show()
                    },
                    onAvatarClick = { showGoogleSignInDialog = true },
                    onThemeClick = { showThemeDialog = true }
                )
            }
        },
        bottomBar = {
            if (playerState.currentVideo == null || isPlayerMinimized) {
                Column {
                    // Mini-Player docked right above bottom navigation bar or during search
                    if (playerState.currentVideo != null && isPlayerMinimized) {
                        MiniPlayerBar(
                            state = playerState,
                            controller = playerController,
                            onExpand = { isPlayerMinimized = false },
                            onClose = {
                                playerController.closePlayer()
                                isPlayerMinimized = false
                            }
                        )
                    }

                    if (!isSearchActive) {
                        // Main 5-Tab YouTube Navigation Bar
                        NavigationBar(
                            containerColor = currentTheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("youtube_bottom_nav")
                        ) {
                        // 1. Home
                        NavigationBarItem(
                            selected = currentTab == MainTab.HOME,
                            onClick = { currentTab = MainTab.HOME },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontSize = 10.sp) },
                            colors = navItemColors(currentTheme)
                        )

                        // 2. Shorts
                        NavigationBarItem(
                            selected = currentTab == MainTab.SHORTS,
                            onClick = { currentTab = MainTab.SHORTS },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.SHORTS) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                                    contentDescription = "Shorts"
                                )
                            },
                            label = { Text("Shorts", fontSize = 10.sp) },
                            colors = navItemColors(currentTheme)
                        )

                        // 3. Add / Upload Button (Center + icon)
                        NavigationBarItem(
                            selected = false,
                            onClick = { showUploadDialog = true },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(currentTheme.primary.copy(alpha = 0.15f), CircleShape)
                                        .border(1.5.dp, currentTheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create / Upload",
                                        tint = currentTheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = null,
                            colors = navItemColors(currentTheme)
                        )

                        // 4. Subscriptions
                        NavigationBarItem(
                            selected = currentTab == MainTab.SUBSCRIPTIONS,
                            onClick = { currentTab = MainTab.SUBSCRIPTIONS },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == MainTab.SUBSCRIPTIONS) Icons.Filled.Subscriptions else Icons.Outlined.Subscriptions,
                                    contentDescription = "Subscriptions"
                                )
                            },
                            label = { Text("Subscriptions", fontSize = 10.sp) },
                            colors = navItemColors(currentTheme)
                        )

                        // 5. You
                        NavigationBarItem(
                            selected = currentTab == MainTab.YOU,
                            onClick = { currentTab = MainTab.YOU },
                            icon = {
                                if (currentAccount != null) {
                                    AsyncImage(
                                        model = currentAccount!!.avatarUrl,
                                        contentDescription = "You",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (currentTab == MainTab.YOU) 1.5.dp else 0.dp,
                                                color = if (currentTab == MainTab.YOU) currentTheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "You"
                                    )
                                }
                            },
                            label = { Text("You", fontSize = 10.sp) },
                            colors = navItemColors(currentTheme)
                        )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(currentTheme.background)
        ) {
            // Main tab contents
            if (isSearchActive) {
                SearchScreen(
                    videos = videos,
                    onBack = { isSearchActive = false },
                    onVideoClick = { video ->
                        isSearchActive = false
                        playerController.setVideo(video)
                        isPlayerMinimized = false
                        scope.launch { repository.addToHistory(video, 0) }
                    },
                    onSaveWatchLater = { video ->
                        scope.launch { repository.toggleWatchLater(video) }
                        Toast.makeText(context, "Saved to Watch Later", Toast.LENGTH_SHORT).show()
                    },
                    onDownload = { video ->
                        scope.launch { repository.downloadVideo(video) }
                        Toast.makeText(context, "Downloading in 1080p offline...", Toast.LENGTH_SHORT).show()
                    },
                    onShare = { video ->
                        Toast.makeText(context, "Link copied: https://youtu.be/${video.id}", Toast.LENGTH_SHORT).show()
                    },
                    onChannelClick = {
                        Toast.makeText(context, "Opening channel", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                when (currentTab) {
                    MainTab.HOME -> HomeScreen(
                        videos = videos,
                        shorts = shorts,
                        currentAccount = currentAccount,
                        onVideoClick = { video ->
                            playerController.setVideo(video)
                            isPlayerMinimized = false
                            scope.launch { repository.addToHistory(video, 0) }
                        },
                        onShortClick = { short ->
                            currentTab = MainTab.SHORTS
                        },
                        onSaveWatchLater = { video ->
                            scope.launch { repository.toggleWatchLater(video) }
                            Toast.makeText(context, "Saved to Watch Later", Toast.LENGTH_SHORT).show()
                        },
                        onDownload = { video ->
                            scope.launch { repository.downloadVideo(video) }
                            Toast.makeText(context, "Downloading in 1080p offline...", Toast.LENGTH_SHORT).show()
                        },
                        onShare = { video ->
                            Toast.makeText(context, "Link copied: https://youtu.be/${video.id}", Toast.LENGTH_SHORT).show()
                        },
                        onChannelClick = {
                            Toast.makeText(context, "Channel selected", Toast.LENGTH_SHORT).show()
                        }
                    )

                    MainTab.SHORTS -> ShortsScreen(
                        shorts = shorts,
                        currentAccount = currentAccount
                    )

                    MainTab.ADD -> {
                        // Handled via Dialog
                    }

                    MainTab.SUBSCRIPTIONS -> SubscriptionsScreen(
                        videos = videos,
                        subscribedMap = subscribedMap,
                        onVideoClick = { video ->
                            playerController.setVideo(video)
                            isPlayerMinimized = false
                            scope.launch { repository.addToHistory(video, 0) }
                        },
                        onSaveWatchLater = { video ->
                            scope.launch { repository.toggleWatchLater(video) }
                        },
                        onDownload = { video ->
                            scope.launch { repository.downloadVideo(video) }
                        },
                        onShare = {},
                        onChannelClick = {}
                    )

                    MainTab.YOU -> YouScreen(
                        currentAccount = currentAccount,
                        historyVideos = historyVideos,
                        downloads = offlineVideos,
                        watchLaterList = watchLaterList,
                        playlists = customPlaylists,
                        isAmbientMode = isAmbientModeRepo,
                        isBackgroundPlayback = isBackgroundPlayback,
                        isRestrictedMode = isRestrictedMode,
                        currentTheme = currentTheme,
                        onSelectTheme = { repository.setTheme(it) },
                        onOpenThemeDialog = { showThemeDialog = true },
                        onOpenGoogleSignIn = { showGoogleSignInDialog = true },
                        onSignOut = {
                            repository.signOut()
                            Toast.makeText(context, "সমস্ত জিমেইল অ্যাকাউন্ট থেকে লগ আউট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onOpenTimeWatched = { showWatchTimeDialog = true },
                        onOpenStudio = { showStudioDialog = true },
                        onClearHistory = {
                            scope.launch { repository.clearHistory() }
                            Toast.makeText(context, "Watch history cleared", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteDownload = { id ->
                            scope.launch { repository.removeOfflineVideo(id) }
                            Toast.makeText(context, "Offline video removed", Toast.LENGTH_SHORT).show()
                        },
                        onToggleAmbientMode = {
                            repository.toggleAmbientMode()
                            playerController.toggleAmbientMode()
                        },
                        onToggleBackgroundPlayback = { repository.toggleBackgroundPlayback() },
                        onToggleRestrictedMode = { repository.toggleRestrictedMode() },
                        onPlayHistoryVideo = { hist ->
                            val match = videos.firstOrNull { it.id == hist.videoId } ?: videos.firstOrNull()
                            if (match != null) {
                                playerController.setVideo(match)
                                isPlayerMinimized = false
                            }
                        },
                        onPlayOfflineVideo = { off ->
                            val match = videos.firstOrNull { it.id == off.videoId } ?: videos.firstOrNull()
                            if (match != null) {
                                playerController.setVideo(match)
                                isPlayerMinimized = false
                            }
                        }
                    )
                }
            }

            // Full Video Player Overlay
            if (playerState.currentVideo != null && !isPlayerMinimized) {
                val currentVideo = playerState.currentVideo!!
                val isLiked = likedVideoIds.contains(currentVideo.id)
                val isDisliked = dislikedVideoIds.contains(currentVideo.id)
                val isSubscribed = subscribedMap.containsKey(currentVideo.channelId)
                val bellMode = subscribedMap[currentVideo.channelId] ?: BellNotificationMode.NONE
                val isDownloaded = offlineVideos.any { it.videoId == currentVideo.id }
                val isWatchLater = watchLaterList.any { it.videoId == currentVideo.id }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(currentTheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Video Player View (Ambient mode, gestures, scrubber, speed, CC, quality)
                    VideoPlayerView(
                        controller = playerController,
                        state = playerState,
                        onCloseOrMinimize = { isPlayerMinimized = true }
                    )

                    // Video Detail (Channels, Like, Dislike, Share, Download, Pinned Comment, Recommended)
                    VideoDetailView(
                        video = currentVideo,
                        playerState = playerState,
                        isLiked = isLiked,
                        isDisliked = isDisliked,
                        isSubscribed = isSubscribed,
                        bellMode = bellMode,
                        isDownloaded = isDownloaded,
                        isWatchLater = isWatchLater,
                        onLikeToggle = { repository.toggleLike(currentVideo.id) },
                        onDislikeToggle = { repository.toggleDislike(currentVideo.id) },
                        onSubscribeToggle = { repository.toggleSubscription(currentVideo.channelId) },
                        onBellModeChange = { mode -> repository.setBellNotification(currentVideo.channelId, mode) },
                        onDownloadClick = {
                            scope.launch {
                                repository.downloadVideo(currentVideo)
                                Toast.makeText(context, "Saved for offline playback in Room DB", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onWatchLaterToggle = {
                            scope.launch {
                                repository.toggleWatchLater(currentVideo)
                                Toast.makeText(context, "Updated Watch Later", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenComments = { showCommentsSheet = true },
                        onChannelClick = {
                            Toast.makeText(context, "Channel: ${currentVideo.channelName}", Toast.LENGTH_SHORT).show()
                        },
                        onSelectRecommendedVideo = { nextVideo ->
                            playerController.setVideo(nextVideo)
                            scope.launch { repository.addToHistory(nextVideo, 0) }
                        }
                    )
                }
            }

            // Interactive Dialogs:
            // 1. Google Sign-In Dialog ("Continue with Google")
            if (showGoogleSignInDialog) {
                GoogleSignInDialog(
                    currentAccount = currentAccount,
                    onSelectAccount = { account ->
                        repository.signInWithGoogle(account)
                        Toast.makeText(context, "Signed in as ${account.displayName}", Toast.LENGTH_SHORT).show()
                    },
                    onSignOut = {
                        repository.signOut()
                        Toast.makeText(context, "Using YouTube as Guest", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showGoogleSignInDialog = false }
                )
            }

            // 2. Upload Video Dialog (YouTube Studio)
            if (showUploadDialog) {
                UploadVideoDialog(
                    onUploadComplete = { title, desc, cat, vis, thumb ->
                        repository.uploadVideo(title, desc, cat, vis, thumb)
                    },
                    onDismiss = { showUploadDialog = false }
                )
            }

            // 3. Time Watched & Wellbeing Dialog
            if (showWatchTimeDialog) {
                WatchTimeStatsDialog(
                    isRestrictedMode = isRestrictedMode,
                    onToggleRestrictedMode = { repository.toggleRestrictedMode() },
                    onDismiss = { showWatchTimeDialog = false }
                )
            }

            // 4. YouTube Studio Dashboard Dialog
            if (showStudioDialog) {
                StudioDashboardDialog(
                    currentAccount = currentAccount,
                    onOpenUpload = { showUploadDialog = true },
                    onDismiss = { showStudioDialog = false }
                )
            }

            // 5. Threaded Comments Bottom Sheet
            if (showCommentsSheet && playerState.currentVideo != null) {
                CommentsBottomSheet(
                    videoId = playerState.currentVideo!!.id,
                    currentAccount = currentAccount,
                    onTimestampClick = { timestampSec ->
                        playerController.seekTo(timestampSec * 1000L)
                        showCommentsSheet = false
                    },
                    onDismiss = { showCommentsSheet = false }
                )
            }

            // 6. App Theme & Color Customization Dialog
            if (showThemeDialog) {
                ThemeSelectionDialog(
                    currentTheme = currentTheme,
                    onSelectTheme = { preset ->
                        repository.setTheme(preset)
                        Toast.makeText(context, "${preset.nameBn} সিলেক্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = { showThemeDialog = false }
                )
            }
        }
    }
}

@Composable
private fun navItemColors(theme: AppThemePreset) = NavigationBarItemDefaults.colors(
    selectedIconColor = theme.primary,
    selectedTextColor = theme.primary,
    unselectedIconColor = theme.onSurfaceVariant,
    unselectedTextColor = theme.onSurfaceVariant,
    indicatorColor = theme.primary.copy(alpha = 0.15f)
)
