package com.example.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Rational
import android.view.Surface
import androidx.compose.ui.graphics.Color
import com.example.model.Chapter
import com.example.model.VideoItem
import com.example.model.VideoQuality
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

    fun closePlayer() {
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
        initMediaPlayer(video)
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
        if (currentlyPlaying) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        playerState.update { it.copy(isPlaying = !currentlyPlaying) }
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0, playerState.value.durationMs)
        try {
            mediaPlayer?.seekTo(clamped.toInt())
        } catch (_: Exception) {}
        playerState.update { it.copy(currentPositionMs = clamped) }
        updateSubtitles(clamped)
    }

    fun seekRelative(secondsDelta: Int) {
        val target = playerState.value.currentPositionMs + (secondsDelta * 1000L)
        seekTo(target)
        showGestureFeedback(GestureFeedback.Seek(secondsDelta, isForward = secondsDelta > 0))
    }

    fun setPlaybackSpeed(speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer?.let {
                    val params = it.playbackParams
                    params.speed = speed
                    it.playbackParams = params
                }
            }
        } catch (_: Exception) {}
        playerState.update { it.copy(playbackSpeed = speed) }
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
    }
}
