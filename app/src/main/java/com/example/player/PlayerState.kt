package com.example.player

import androidx.compose.ui.graphics.Color
import com.example.model.Chapter
import com.example.model.SubtitleLine
import com.example.model.VideoItem
import com.example.model.VideoQuality

data class PlayerState(
    val currentVideo: VideoItem? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0,
    val durationMs: Long = 0,
    val bufferedPositionMs: Long = 0,
    val playbackSpeed: Float = 1.0f,
    val selectedQuality: VideoQuality = VideoQuality("Auto (1080p)", 4500),
    val isAmbientMode: Boolean = true,
    val isSubtitlesEnabled: Boolean = false,
    val selectedSubtitleLanguage: String = "English",
    val activeSubtitleText: String? = null,
    val isAudioOnlyMode: Boolean = false,
    val isMiniPlayer: Boolean = false,
    val isFullscreen: Boolean = false,
    val volumeLevel: Float = 0.8f,     // 0.0 to 1.0
    val brightnessLevel: Float = 0.7f, // 0.0 to 1.0
    val gestureFeedback: GestureFeedback? = null
)

sealed class GestureFeedback {
    data class Seek(val secondsDelta: Int, val isForward: Boolean) : GestureFeedback()
    data class Volume(val percent: Int) : GestureFeedback()
    data class Brightness(val percent: Int) : GestureFeedback()
}
