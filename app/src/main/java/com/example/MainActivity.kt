package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.data.local.YouTubeDatabase
import com.example.data.repository.YouTubeRepository
import com.example.player.VideoPlayerController
import com.example.ui.YouTubeApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private lateinit var playerController: VideoPlayerController
  private lateinit var repository: YouTubeRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = YouTubeDatabase.getDatabase(applicationContext)
    repository = YouTubeRepository(
      dao = database.youTubeDao(),
      prefs = getSharedPreferences("mytube_prefs", Context.MODE_PRIVATE)
    )
    playerController = VideoPlayerController(this, lifecycleScope)

    // Background audio playback:
    // If the user enabled "Background Audio Playback", the player keeps going (wake lock +
    // audio focus are held by the controller). If disabled, pause when the app is hidden.
    lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onStop(owner: LifecycleOwner) {
        if (!repository.isBackgroundPlaybackEnabled.value) {
          playerController.pauseForBackground()
        }
      }

      override fun onStart(owner: LifecycleOwner) {
        playerController.resumeFromBackground()
      }
    })

    setContent {
      val currentTheme by repository.currentTheme.collectAsState()
      MyApplicationTheme(themePreset = currentTheme) {
        YouTubeApp(
          repository = repository,
          playerController = playerController
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::playerController.isInitialized) {
      playerController.releasePlayer()
    }
  }
}
