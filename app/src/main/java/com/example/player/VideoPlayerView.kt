package com.example.player

import android.app.Activity
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.model.Chapter
import com.example.model.VideoItem
import com.example.model.VideoQuality
import com.example.model.isYouTubeVideo
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerView(
    controller: VideoPlayerController,
    state: PlayerState,
    onCloseOrMinimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val video = state.currentVideo ?: return

    var areControlsVisible by remember { mutableStateOf(true) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showChaptersSheet by remember { mutableStateOf(false) }
    var showSubtitleSheet by remember { mutableStateOf(false) }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(areControlsVisible, state.isPlaying) {
        if (areControlsVisible && state.isPlaying) {
            delay(4000)
            areControlsVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .testTag("video_player_container")
    ) {
        // 1. Ambient Mode Glow
        if (state.isAmbientMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val glowBrush = Brush.radialGradient(
                    colors = listOf(
                        video.ambientColor.copy(alpha = 0.45f),
                        video.ambientColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    radius = size.maxDimension * 0.7f
                )
                drawRect(brush = glowBrush)
            }
        }

        val isYouTube = isYouTubeVideo(video)

        // 2. Video Player Frame / Audio-Only Screen
        if (state.isAudioOnlyMode) {
            // Audio-Only Premium Mode (Data Saver)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF141414)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(YouTubeRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Audio Only Mode",
                            tint = YouTubeRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Audio-Only Mode (Data Saver)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Video rendering paused • Saving 85% mobile data",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }
            }
        } else if (isYouTube) {
            // Real YouTube Player via Hardware Accelerated WebView.
            // The plain embed URL is the reliable way to play YouTube in a WebView:
            // mediaPlaybackRequiresUserGesture=false lets it autoplay, and the embed
            // player provides its own controls (play/pause, seek, speed, quality).
            val ytId = if (video.id.length == 11) video.id else {
                val uri = android.net.Uri.parse(video.videoUrl)
                uri.getQueryParameter("v") ?: video.id
            }
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        webChromeClient = WebChromeClient()
                        webViewClient = WebViewClient()
                        loadUrl("https://www.youtube.com/embed/$ytId?autoplay=1&playsinline=1&rel=0&modestbranding=1&iv_load_policy=3")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Direct MP4 / HLS Video Surface via TextureView (decodes and displays video stream)
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).apply {
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                                controller.attachSurface(Surface(st))
                            }

                            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}

                            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                controller.detachSurface()
                                return true
                            }

                            override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Thumbnail shown until playback starts
            if (state.currentPositionMs == 0L && !state.isPlaying) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 3. Subtitles Overlay (CC)
        if (state.isSubtitlesEnabled && !state.activeSubtitleText.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (areControlsVisible) 56.dp else 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = state.activeSubtitleText,
                    color = Color.Yellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (!isYouTube) {
        // 4. Gesture Detection Overlay
        // Tap to toggle controls, Double-tap left to -10s, Double-tap right to +10s, Vertical drags for volume/brightness
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            areControlsVisible = !areControlsVisible
                        },
                        onDoubleTap = { offset ->
                            val screenWidth = size.width
                            if (offset.x < screenWidth * 0.45f) {
                                controller.seekRelative(-10)
                            } else if (offset.x > screenWidth * 0.55f) {
                                controller.seekRelative(10)
                            } else {
                                controller.playPause()
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        val screenWidth = size.width
                        val isLeftHalf = change.position.x < screenWidth * 0.5f
                        val delta = -dragAmount / 350f
                        if (isLeftHalf) {
                            controller.setBrightnessLevel(state.brightnessLevel + delta)
                        } else {
                            controller.setVolumeLevel(state.volumeLevel + delta)
                        }
                    }
                }
        )

        // 5. Gesture Feedback Indicator
        state.gestureFeedback?.let { feedback ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    when (feedback) {
                        is GestureFeedback.Seek -> {
                            Icon(
                                imageVector = if (feedback.isForward) Icons.Default.Forward10 else Icons.Default.Replay10,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${if (feedback.isForward) "+" else ""}${feedback.secondsDelta}s",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is GestureFeedback.Volume -> {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Volume: ${feedback.percent}%", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        is GestureFeedback.Brightness -> {
                            Icon(imageVector = Icons.Default.LightMode, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Brightness: ${feedback.percent}%", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Interactive Player Controls (Animated Visibility)
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCloseOrMinimize,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize Player",
                            tint = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Ambient Mode Toggle
                        IconButton(
                            onClick = { controller.toggleAmbientMode() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = "Ambient Mode",
                                tint = if (state.isAmbientMode) YouTubePremiumGold else Color.White.copy(alpha = 0.6f)
                            )
                        }

                        // Audio-Only Mode Toggle
                        IconButton(
                            onClick = { controller.toggleAudioOnlyMode() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Audio-Only Mode",
                                tint = if (state.isAudioOnlyMode) YouTubeRed else Color.White.copy(alpha = 0.6f)
                            )
                        }

                        // CC Subtitles Toggle
                        IconButton(
                            onClick = {
                                if (!state.isSubtitlesEnabled && video.subtitles.isNotEmpty()) {
                                    showSubtitleSheet = true
                                } else {
                                    controller.toggleSubtitles()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isSubtitlesEnabled) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionOff,
                                contentDescription = "Subtitles",
                                tint = if (state.isSubtitlesEnabled) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        }

                        // Picture in Picture (PiP)
                        IconButton(
                            onClick = {
                                (context as? Activity)?.let { controller.enterPip(it) }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureInPicture,
                                contentDescription = "Picture in Picture",
                                tint = Color.White
                            )
                        }

                        // Settings (Speed, Quality, Chapters)
                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Player Settings",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Center Play/Pause & 10s Rewind/Forward Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { controller.seekRelative(-10) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { controller.playPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    IconButton(
                        onClick = { controller.seekRelative(10) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom Timeline, Chapter Pill & Fullscreen Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    // Chapter title pill if available
                    val activeChapter = controller.getCurrentChapter()
                    if (activeChapter != null || video.chapters.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { showChaptersSheet = true }
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• Chapter: ${activeChapter?.title ?: "View Chapters"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Timeline Scrubber
                    val currentSec = state.currentPositionMs / 1000
                    val totalSec = state.durationMs / 1000
                    val progress = if (state.durationMs > 0) {
                        (state.currentPositionMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Slider(
                        value = progress,
                        onValueChange = { frac ->
                            controller.seekTo((frac * state.durationMs).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = YouTubeRed,
                            activeTrackColor = YouTubeRed,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatTime(currentSec)} / ${formatTime(totalSec)}",
                            color = Color.White,
                            fontSize = 12.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.playbackSpeed != 1.0f) {
                                Text(
                                    text = "${state.playbackSpeed}x",
                                    color = YouTubePremiumGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Text(
                                text = state.selectedQuality.label.take(10),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        } else {
            // Floating Minimize button for YouTube WebView (the embed has its own controls)
            IconButton(
                onClick = onCloseOrMinimize,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimize Player",
                    tint = Color.White
                )
            }
        }
    }

    // 7. Settings Modal Bottom Sheet (Quality, Speed, Subtitles, Chapters)
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color(0xFF212121),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Playback Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Quality Selector
                SettingRowItem(
                    icon = Icons.Default.Settings,
                    title = "Quality",
                    currentValue = state.selectedQuality.label,
                    onClick = {
                        // Switch through qualities
                        val qualities = video.availableQualities
                        val nextIdx = (qualities.indexOf(state.selectedQuality) + 1) % qualities.size
                        controller.setQuality(qualities[nextIdx])
                    }
                )

                // Playback Speed Selector (0.25x to 3.0x)
                val speeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
                SettingRowItem(
                    icon = Icons.Default.Speed,
                    title = "Playback Speed",
                    currentValue = "${state.playbackSpeed}x",
                    onClick = {
                        val nextSpeedIdx = (speeds.indexOf(state.playbackSpeed) + 1).let {
                            if (it >= speeds.size) 0 else it
                        }
                        controller.setPlaybackSpeed(speeds[nextSpeedIdx])
                    }
                )

                // Subtitle / CC
                SettingRowItem(
                    icon = Icons.Default.ClosedCaption,
                    title = "Captions (CC)",
                    currentValue = if (state.isSubtitlesEnabled) state.selectedSubtitleLanguage else "Off",
                    onClick = {
                        showSettingsSheet = false
                        showSubtitleSheet = true
                    }
                )

                // Chapters
                if (video.chapters.isNotEmpty()) {
                    SettingRowItem(
                        icon = Icons.Default.GraphicEq,
                        title = "Chapters",
                        currentValue = "${video.chapters.size} segments",
                        onClick = {
                            showSettingsSheet = false
                            showChaptersSheet = true
                        }
                    )
                }

                // Ambient Mode
                SettingRowItem(
                    icon = Icons.Default.LightMode,
                    title = "Ambient Mode",
                    currentValue = if (state.isAmbientMode) "On" else "Off",
                    onClick = { controller.toggleAmbientMode() }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 8. Chapters List Bottom Sheet
    if (showChaptersSheet && video.chapters.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showChaptersSheet = false },
            containerColor = Color(0xFF212121),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Video Chapters",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                video.chapters.forEach { chapter ->
                    val isActive = controller.getCurrentChapter() == chapter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) YouTubeRed.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                controller.seekTo(chapter.startSecond * 1000L)
                                showChaptersSheet = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(chapter.startSecond.toLong()),
                            color = if (isActive) YouTubeRed else YouTubePremiumGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(54.dp)
                        )
                        Text(
                            text = chapter.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 9. Subtitle Language Picker Bottom Sheet
    if (showSubtitleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSubtitleSheet = false },
            containerColor = Color(0xFF212121),
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Captions Language",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val languages = listOf("Off", "English", "Bengali", "Spanish")
                languages.forEach { lang ->
                    val isSelected = if (lang == "Off") !state.isSubtitlesEnabled else (state.isSubtitlesEnabled && state.selectedSubtitleLanguage == lang)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) YouTubeRed.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                if (lang == "Off") {
                                    if (state.isSubtitlesEnabled) controller.toggleSubtitles()
                                } else {
                                    controller.selectSubtitleLanguage(lang)
                                }
                                showSubtitleSheet = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lang,
                            color = if (isSelected) YouTubeRed else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text("✓", color = YouTubeRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = Color.White, fontSize = 15.sp)
        }
        Text(
            text = currentValue,
            color = Color(0xFFAAAAAA),
            fontSize = 13.sp
        )
    }
}

private fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
}
