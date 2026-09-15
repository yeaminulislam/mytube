package com.example.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.os.PowerManager
import android.util.Rational
import android.view.Surface
import android.webkit.WebView
import com.example.model.Chapter
import com.example.model.VideoItem
import com.example.model.VideoQuality
import com.example.model.isYouTubeVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VideoPlayerController(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var positionTrackerJob: Job? = null
    private var currentSurface: Surface? = null

    /**
     * Hook used to drive the real YouTube IFrame player that runs inside the WebView.
     * Set by [attachWebEngine] while the player overlay is composed, cleared by [detachWebEngine].
     */
    var webCommandSink: ((js: String) -> Unit)? = null
        private set

    // --- Audio focus & wake lock so playback keeps going in the background (Premium-style) ---
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private val wakeLock = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTube:VideoPlayback")
    private var isPausedForBackground = false

    private val audioFocusListener = object : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pauseForBackground()

                AudioManager.AUDIOFOCUS_GAIN -> resumeFromBackground()
            }
        }
    }

    val playerState = MutableStateFlow(PlayerState())

    fun attachSurface(surface: Surface) {
        currentSurface = surface
        try {
            mediaPlayer?.setSurface(surface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun detachSurface() {
        currentSurface = null
        try {
            mediaPlayer?.setSurface(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Registers the WebView that hosts the YouTube IFrame player for the current video. */
    fun attachWebEngine(webView: WebView) {
        webCommandSink = { js ->
            try {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    webView.post { webView.evaluateJavascript(js, null) }
                } else {
                    webView.evaluateJavascript(js, null)
                }
            } catch (_: Exception) {}
        }
    }

    /** Clears the WebView engine (player overlay removed / video closed). */
    fun detachWebEngine() {
        webCommandSink = null
    }

    fun closePlayer() {
        detachWebEngine()
        releasePlayer()
        playerState.update {
            it.copy(
                currentVideo = null,
                isPlaying = false,
                currentPositionMs = 0,
                durationMs = 0
            )
        }
    }

    fun setVideo(video: VideoItem) {
        releasePlayer()
        playerState.update {
            it.copy(
                currentVideo = video,
                currentPositionMs = 0,
                durationMs = video.durationSeconds * 1000L,
                bufferedPositionMs = (video.durationSeconds * 0.85 * 1000L).toLong(),
                isPlaying = true,
                activeSubtitleText = null,
                isAudioOnlyMode = false
            )
        }
        isPausedForBackground = false
        if (!isYouTubeVideo(video)) {
            // Only plain MP4/HLS links need a native MediaPlayer.
            // YouTube videos are driven by the WebView IFrame engine instead.
            initMediaPlayer(video)
        }
        updatePlaybackLocks(true)
        startPositionTracker()
    }

    private fun initMediaPlayer(video: VideoItem) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(video.videoUrl))
                currentSurface?.let {
                    if (it.isValid) setSurface(it)
                }
                setOnPreparedListener { mp ->
                    currentSurface?.let {
                        if (it.isValid) mp.setSurface(it)
                    }
                    mp.start()
                    setPlaybackSpeed(playerState.value.playbackSpeed)
                    playerState.update {
                        it.copy(
                            isPlaying = true,
                            durationMs = if (mp.duration > 0) mp.duration.toLong() else video.durationSeconds * 1000L
                        )
                    }
                }
                setOnCompletionListener {
                    playerState.update { it.copy(isPlaying = false, currentPositionMs = it.durationMs) }
                    updatePlaybackLocks(false)
                }
                setOnErrorListener { _, _, _ ->
                    // Fallback to synthetic duration tracking
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            // Fallback for offline or local preview
            playerState.update { it.copy(isPlaying = true) }
        }
    }

    fun playPause() {
        val currentlyPlaying = playerState.value.isPlaying
        if (webCommandSink != null) {
            // YouTube IFrame engine — send the command, state comes back via onEvent.
            webCommandSink!!.invoke(if (currentlyPlaying) "ytPause()" else "ytPlay()")
        } else if (currentlyPlaying) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        if (webCommandSink == null) {
            playerState.update { it.copy(isPlaying = !currentlyPlaying) }
        }
        updatePlaybackLocks(!currentlyPlaying)
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0, playerState.value.durationMs)
        if (webCommandSink != null) {
            webCommandSink!!.invoke("ytSeek(${clamped / 1000})")
        } else {
            try {
                mediaPlayer?.seekTo(clamped.toInt())
            } catch (_: Exception) {}
        }
        playerState.update { it.copy(currentPositionMs = clamped) }
        updateSubtitles(clamped)
    }

    fun seekRelative(secondsDelta: Int) {
        val target = playerState.value.currentPositionMs + (secondsDelta * 1000L)
        seekTo(target)
        showGestureFeedback(GestureFeedback.Seek(secondsDelta, isForward = secondsDelta > 0))
    }

    fun setPlaybackSpeed(speed: Float) {
        if (webCommandSink != null) {
            webCommandSink!!.invoke("ytSpeed($speed)")
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mediaPlayer?.let {
                        val params = it.playbackParams
                        params.speed = speed
                        it.playbackParams = params
                    }
                }
            } catch (_: Exception) {}
        }
        playerState.update { it.copy(playbackSpeed = speed) }
    }

    /**
     * Called from the WebView JS bridge with the real IFrame player state:
     * [timeMs] current position, [durationMs] duration, [playing] whether it is playing/buffering.
     */
    fun reportWebPlayback(timeMs: Long, durationMs: Long, playing: Boolean) {
        val wasPlaying = playerState.value.isPlaying
        playerState.update {
            val duration = if (durationMs > 0) durationMs else it.durationMs
            it.copy(
                currentPositionMs = if (duration > 0) timeMs.coerceIn(0, duration) else timeMs,
                durationMs = duration,
                isPlaying = playing
            )
        }
        if (playing != wasPlaying) updatePlaybackLocks(playing)
        updateSubtitles(playerState.value.currentPositionMs)
    }

    fun setQuality(quality: VideoQuality) {
        playerState.update { it.copy(selectedQuality = quality) }
    }

    fun toggleAmbientMode() {
        playerState.update { it.copy(isAmbientMode = !it.isAmbientMode) }
    }

    fun toggleSubtitles() {
        playerState.update {
            val newState = !it.isSubtitlesEnabled
            it.copy(
                isSubtitlesEnabled = newState,
                activeSubtitleText = if (newState) it.activeSubtitleText else null
            )
        }
    }

    fun selectSubtitleLanguage(language: String) {
        playerState.update {
            it.copy(selectedSubtitleLanguage = language, isSubtitlesEnabled = true)
        }
        updateSubtitles(playerState.value.currentPositionMs)
    }

    fun toggleAudioOnlyMode() {
        playerState.update { it.copy(isAudioOnlyMode = !it.isAudioOnlyMode) }
    }

    fun setVolumeLevel(level: Float) {
        val clamped = level.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (_: Exception) {}
        playerState.update { it.copy(volumeLevel = clamped) }
        showGestureFeedback(GestureFeedback.Volume((clamped * 100).toInt()))
    }

    fun setBrightnessLevel(level: Float) {
        val clamped = level.coerceIn(0.1f, 1f)
        playerState.update { it.copy(brightnessLevel = clamped) }
        showGestureFeedback(GestureFeedback.Brightness((clamped * 100).toInt()))
    }

    fun toggleMiniPlayer(mini: Boolean) {
        playerState.update { it.copy(isMiniPlayer = mini) }
    }

    fun enterPip(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(16, 9)
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            activity.enterPictureInPictureMode(pipParams)
        }
    }

    // --- Background playback (app minimized / screen off) ---

    /** Pauses playback when the app goes to background and background playback is disabled. */
    fun pauseForBackground() {
        if (!playerState.value.isPlaying) return
        isPausedForBackground = true
        if (webCommandSink != null) {
            webCommandSink!!.invoke("ytPause()")
        } else {
            try {
                mediaPlayer?.pause()
            } catch (_: Exception) {}
        }
        playerState.update { it.copy(isPlaying = false) }
        updatePlaybackLocks(false)
    }

    /** Resumes playback that was paused for backgrounding, once the app is visible again. */
    fun resumeFromBackground() {
        if (!isPausedForBackground) return
        isPausedForBackground = false
        if (webCommandSink != null) {
            webCommandSink!!.invoke("ytPlay()")
        } else {
            try {
                mediaPlayer?.start()
            } catch (_: Exception) {}
        }
        playerState.update { it.copy(isPlaying = true) }
        updatePlaybackLocks(true)
    }

    private fun updatePlaybackLocks(playing: Boolean) {
        try {
            if (playing) {
                if (!wakeLock.isHeld) wakeLock.acquire(10 * 60 * 60 * 1000L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val request = AudioFocusRequest.Builder(AudioManager.STREAM_MUSIC)
                        .setAudioAttributes(
                            android.media.AudioAttributes.Builder()
                                .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MOVIE)
                                .build()
                        )
                        .setOnAudioFocusChangeListener(audioFocusListener)
                        .setRequestType(AudioManager.AUDIOFOCUS_GAIN)
                        .build()
                    audioManager.requestAudioFocus(request)
                    audioFocusRequest = request
                } else {
                    @Suppress("DEPRECATION")
                    audioManager.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                }
            } else {
                if (wakeLock.isHeld) wakeLock.release()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
                } else {
                    @Suppress("DEPRECATION")
                    audioManager.abandonAudioFocus(audioFocusListener)
                }
                audioFocusRequest = null
            }
        } catch (_: Exception) {}
    }

    private fun showGestureFeedback(feedback: GestureFeedback) {
        playerState.update { it.copy(gestureFeedback = feedback) }
        scope.launch {
            delay(1200)
            if (playerState.value.gestureFeedback == feedback) {
                playerState.update { it.copy(gestureFeedback = null) }
            }
        }
    }

    private fun startPositionTracker() {
        positionTrackerJob?.cancel()
        positionTrackerJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                if (playerState.value.isPlaying) {
                    if (webCommandSink != null) {
                        // YouTube videos: position is reported by the WebView bridge instead.
                        delay(500)
                        continue
                    }
                    val current = try {
                        mediaPlayer?.currentPosition?.toLong() ?: (playerState.value.currentPositionMs + 500)
                    } catch (e: Exception) {
                        playerState.value.currentPositionMs + 500
                    }
                    val duration = playerState.value.durationMs
                    val clamped = if (duration > 0) current.coerceIn(0, duration) else current
                    playerState.update { it.copy(currentPositionMs = clamped) }
                    updateSubtitles(clamped)
                }
                delay(500)
            }
        }
    }

    private fun updateSubtitles(positionMs: Long) {
        val state = playerState.value
        if (!state.isSubtitlesEnabled || state.currentVideo == null) return
        val currentSec = (positionMs / 1000).toInt()
        val match = state.currentVideo.subtitles.firstOrNull {
            it.language.equals(state.selectedSubtitleLanguage, ignoreCase = true) &&
                    currentSec >= it.startSecond && currentSec <= it.endSecond
        }
        playerState.update { it.copy(activeSubtitleText = match?.text) }
    }

    fun getCurrentChapter(): Chapter? {
        val state = playerState.value
        val video = state.currentVideo ?: return null
        val currentSec = (state.currentPositionMs / 1000).toInt()
        return video.chapters.lastOrNull { currentSec >= it.startSecond }
    }

    fun releasePlayer() {
        positionTrackerJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        updatePlaybackLocks(false)
    }
}
