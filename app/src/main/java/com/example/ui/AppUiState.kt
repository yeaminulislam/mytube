package com.example.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Small holder for top-level UI navigation flags (search screen open / player minimized)
 * so that the Activity's system-back handling can make YouTube-like decisions:
 *   - back on the search screen  -> close search
 *   - back on a playing video     -> minimize player to the mini-player bar (feed stays)
 *   - back anywhere else          -> exit the app (default behavior)
 */
class AppUiState {

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _isPlayerMinimized = MutableStateFlow(false)
    val isPlayerMinimized: StateFlow<Boolean> = _isPlayerMinimized.asStateFlow()

    fun openSearch() {
        _isSearchActive.value = true
    }

    fun closeSearch() {
        _isSearchActive.value = false
    }

    fun setPlayerMinimized(minimized: Boolean) {
        _isPlayerMinimized.value = minimized
    }

    /** A video was opened / player closed -> never in the minimized state. */
    fun setPlayerActive() {
        _isPlayerMinimized.value = false
    }
}
