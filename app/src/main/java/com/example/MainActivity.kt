package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.data.local.YouTubeDatabase
import com.example.data.repository.YouTubeRepository
import com.example.player.VideoPlayerController
import com.example.ui.YouTubeApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private lateinit var playerController: VideoPlayerController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = YouTubeDatabase.getDatabase(applicationContext)
    val repository = YouTubeRepository(database.youTubeDao())
    playerController = VideoPlayerController(this, lifecycleScope)

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
