package com.example.ui.components

import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.model.ShortItem
import com.example.ui.theme.YouTubeRed

/**
 * Lightweight looping video engine for Shorts (TextureView + MediaPlayer).
 * Each short page owns its own engine; it is released when the page leaves composition.
 */
class ShortVideoEngine {
    private var player: MediaPlayer? = null
    private var surface: Surface? = null
    private var preparedUrl: String? = null
    private var isPrepared = false
    private var muted = false

    fun attachSurface(newSurface: Surface) {
        surface = newSurface
        try {
            if (isPrepared && newSurface.isValid) player?.setSurface(newSurface)
        } catch (_: Exception) {}
    }

    fun releaseSurface() {
        try {
            player?.setSurface(null)
        } catch (_: Exception) {}
        surface = null
    }

    fun play(url: String) {
        if (url.isBlank()) return
        if (preparedUrl == url) {
            try {
                if (isPrepared) player?.start()
            } catch (_: Exception) {}
            return
        }
        releaseInternal()
        preparedUrl = url
        isPrepared = false
        try {
            player = MediaPlayer().apply {
                setDataSource(url)
                surface?.let { if (it.isValid) setSurface(it) }
                isLooping = true
                setVolume(if (muted) 0f else 1f, if (muted) 0f else 1f)
                setOnPreparedListener { mp ->
                    isPrepared = true
                    surface?.let { if (it.isValid) mp.setSurface(it) }
                    mp.setVolume(if (muted) 0f else 1f, if (muted) 0f else 1f)
                    try {
                        mp.start()
                    } catch (_: Exception) {}
                }
                setOnErrorListener { _, _, _ -> true }
                prepareAsync()
            }
        } catch (e: Exception) {
            preparedUrl = null
            player = null
        }
    }

    fun pause() {
        try {
            if (isPrepared) player?.pause()
        } catch (_: Exception) {}
    }

    fun setMuted(value: Boolean) {
        muted = value
        try {
            val level = if (value) 0f else 1f
            player?.setVolume(level, level)
        } catch (_: Exception) {}
    }

    fun release() {
        releaseInternal()
    }

    private fun releaseInternal() {
        try {
            player?.stop()
            player?.release()
        } catch (_: Exception) {}
        player = null
        isPrepared = false
        preparedUrl = null
    }
}

@Composable
fun ShortsItemView(
    short: ShortItem,
    isActive: Boolean,
    onOpenComments: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    var isSubscribed by remember { mutableStateOf(short.isSubscribed) }
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }

    // One engine per short, released when the page leaves the composition
    val engine = remember(short.id) { ShortVideoEngine() }

    // Auto-play the visible page, pause the hidden ones
    LaunchedEffect(short.id, isActive) {
        if (isActive) {
            isPlaying = true
            engine.play(short.videoUrl)
        } else {
            engine.pause()
        }
    }

    // Spinning disc animation for audio track
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_item_${short.id}")
    ) {
        // Fullscreen 9:16 Video (real playback)
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(st: android.graphics.SurfaceTexture, w: Int, h: Int) {
                            engine.attachSurface(Surface(st))
                            if (isActive) engine.play(short.videoUrl)
                        }

                        override fun onSurfaceTextureSizeChanged(st: android.graphics.SurfaceTexture, w: Int, h: Int) {}

                        override fun onSurfaceTextureDestroyed(st: android.graphics.SurfaceTexture): Boolean {
                            engine.releaseSurface()
                            return true
                        }

                        override fun onSurfaceTextureUpdated(st: android.graphics.SurfaceTexture) {}
                    }
                }
            },
            onRelease = { engine.release() },
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 400f
                    )
                )
        )

        // Tap anywhere to play / pause
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(short.id) {
                    detectTapGestures(onTap = {
                        if (isPlaying) {
                            isPlaying = false
                            engine.pause()
                        } else {
                            isPlaying = true
                            engine.play(short.videoUrl)
                        }
                    })
                }
        )

        // Big play indicator while paused
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Short",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Right side Action Bar: Mute, Like, Dislike, Comments, Share, Spinning Disc
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mute / Unmute
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isMuted = !isMuted
                        engine.setMuted(isMuted)
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = if (isMuted) YouTubeRed else Color.White
                    )
                }
            }

            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) isDisliked = false
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = "Like Short",
                        tint = if (isLiked) YouTubeRed else Color.White
                    )
                }
                Text(
                    text = formatShortCount(short.likesCount + if (isLiked) 1 else 0),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Dislike
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isDisliked = !isDisliked
                        if (isDisliked) isLiked = false
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                        contentDescription = "Dislike Short",
                        tint = if (isDisliked) Color(0xFF3EA6FF) else Color.White
                    )
                }
                Text(
                    text = "Dislike",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            // Comments
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onOpenComments,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Shorts Comments",
                        tint = Color.White
                    )
                }
                Text(
                    text = formatShortCount(short.commentsCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Short",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            // Spinning Sound Disc
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222222))
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = short.creatorAvatar,
                    contentDescription = "Sound Disc",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Bottom Left Content: Creator Info, Title, Sound Track
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 28.dp, end = 80.dp)
        ) {
            // Channel row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                AsyncImage(
                    model = short.creatorAvatar,
                    contentDescription = short.creatorName,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "@${short.creatorName}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { isSubscribed = !isSubscribed },
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color.Gray.copy(alpha = 0.6f) else YouTubeRed,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isSubscribed) "Subscribed" else "Subscribe",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Title
            Text(
                text = short.title,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sound Track Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Sound track",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = short.soundTrackTitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatShortCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
