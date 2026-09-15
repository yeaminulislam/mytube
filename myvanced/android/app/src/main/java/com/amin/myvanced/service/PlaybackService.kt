package com.amin.myvanced.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * MyVanced - Background Playback Service
 * Vanced এর সবচেয়ে জনপ্রিয় ফিচার - Background এ ভিডিও চলবে!
 * 
 * কিভাবে কাজ করে:
 * 1. Foreground Service - System এটাকে Kill করবে না
 * 2. ExoPlayer - Video/Audio Play
 * 3. MediaSession - Lock Screen Controls
 * 4. Notification - Play/Pause/Next
 */

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        // ExoPlayer - Vanced এর মতো Player
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                playWhenReady = true
                // AdBlock: Block ad URLs
                // SponsorBlock: Auto skip logic here
            }

        // MediaSession for Background Play + Lock Screen Controls
        mediaSession = MediaSession.Builder(this, player)
            .setId("myvanced_playback")
            .build()

        // Create Notification Channel (Android 8+)
        createNotificationChannel()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start Foreground - Important for Background Play!
        val notification = createNotification()
        startForeground(1, notification)

        // Handle actions: Play, Pause, Next, Previous
        when (intent?.action) {
            "ACTION_PLAY" -> player.play()
            "ACTION_PAUSE" -> player.pause()
            "ACTION_NEXT" -> player.seekToNext()
        }

        return START_STICKY // Service will restart if killed
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "myvanced_playback",
            "MyVanced Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background playback controls"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "myvanced_playback")
            .setContentTitle("MyVanced - Playing in background")
            .setContentText("Android Development Tutorial")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .addAction(android.R.drawable.ic_media_pause, "Pause", getPendingIntent("ACTION_PAUSE"))
            .addAction(android.R.drawable.ic_media_next, "Next", getPendingIntent("ACTION_NEXT"))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionCompatToken))
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        mediaSession?.release()
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return null
    }
}

/**
 * SponsorBlock Integration in Player
 */
class SponsorBlockManager(private val player: ExoPlayer) {
    
    private var segments: List<SponsorSegment> = emptyList()

    fun loadSegments(videoId: String) {
        // Call SponsorBlock API: https://sponsor.ajay.app/api/skipSegments?videoID=VIDEO_ID
        // For demo: Mock segments
        segments = listOf(
            SponsorSegment(0.0, 10.0, "intro"),
            SponsorSegment(120.0, 150.0, "sponsor")
        )
    }

    fun checkAndSkip() {
        val currentPos = player.currentPosition / 1000.0 // seconds
        
        for (segment in segments) {
            if (currentPos >= segment.start && currentPos < segment.end) {
                println("⏭️ Skipping ${segment.category}: ${segment.start} -> ${segment.end}")
                player.seekTo((segment.end * 1000).toLong())
                break
            }
        }
    }
}

data class SponsorSegment(
    val start: Double,
    val end: Double,
    val category: String
)
