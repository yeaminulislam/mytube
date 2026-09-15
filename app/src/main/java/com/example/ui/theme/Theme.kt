package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MyApplicationTheme(
  themePreset: AppThemePreset = AppThemePresets.GreenBlack,
  content: @Composable () -> Unit,
) {
  val colorScheme = themePreset.toColorScheme()
  CompositionLocalProvider(LocalAppTheme provides themePreset) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

