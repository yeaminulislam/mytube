package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.network.YouTubeApiService
import com.example.model.VideoItem
import com.example.ui.components.VideoCard
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    videos: List<VideoItem>,
    onBack: () -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onSaveWatchLater: (VideoItem) -> Unit,
    onDownload: (VideoItem) -> Unit,
    onShare: (VideoItem) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isSearchingOnline by remember { mutableStateOf(false) }
    var onlineVideos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var liveSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var hasSearched by remember { mutableStateOf(false) }

    val quickChips = listOf("Trending", "Bangla Song", "Bangla Natok", "Music", "Gaming", "Tech", "Cricket", "Shorts")

    fun triggerSearch(query: String) {
        if (query.isBlank()) return
        searchQuery = query
        isSearchingOnline = true
        hasSearched = true
        keyboardController?.hide()
        scope.launch {
            val results = YouTubeApiService.searchVideos(query)
            onlineVideos = results
            isSearchingOnline = false
        }
    }

    // Live autocomplete suggestions while typing
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(300)
            try {
                liveSuggestions = YouTubeApiService.fetchSuggestions(searchQuery)
            } catch (e: Exception) {
                liveSuggestions = emptyList()
            }
        } else {
            liveSuggestions = emptyList()
        }
    }

    val displayVideos = remember(onlineVideos, videos, searchQuery, hasSearched) {
        if (onlineVideos.isNotEmpty()) {
            onlineVideos
        } else if (hasSearched) {
            videos.filter { video ->
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.channelName.contains(searchQuery, ignoreCase = true) ||
                video.category.contains(searchQuery, ignoreCase = true)
            }
        } else {
            videos
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .testTag("search_screen")
    ) {
        // Search App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = theme.onBackground)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search YouTube...", color = theme.onSurfaceVariant, fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("search_input_field"),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onlineVideos = emptyList()
                            hasSearched = false
                        }) {
                            Icon(Icons.Default.Clear, null, tint = theme.onBackground)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = theme.onBackground,
                    unfocusedTextColor = theme.onBackground,
                    focusedContainerColor = theme.surface,
                    unfocusedContainerColor = theme.surface,
                    focusedBorderColor = theme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { triggerSearch(searchQuery) })
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = { triggerSearch(searchQuery) }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = theme.primary)
            }

            IconButton(onClick = { triggerSearch("Bangla Song") }) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = theme.onBackground)
            }
        }

        // Quick Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickChips.forEach { chip ->
                val isSelected = selectedCategory == chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) theme.primary else theme.surface)
                        .clickable {
                            selectedCategory = chip
                            triggerSearch(chip)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = chip,
                        color = if (isSelected) Color.Black else theme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Loading Spinner while searching real YouTube
        if (isSearchingOnline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = theme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Searching real YouTube for \"$searchQuery\"...", color = theme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }

        // Real-time suggestions when typing and before hitting search
        if (!hasSearched && searchQuery.isNotBlank() && liveSuggestions.isNotEmpty() && !isSearchingOnline) {
            Text(
                text = "Suggestions",
                color = theme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(liveSuggestions) { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { triggerSearch(suggestion) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = theme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(suggestion, color = theme.onBackground, fontSize = 14.sp)
                    }
                }
            }
        } else if (!hasSearched && searchQuery.isBlank() && !isSearchingOnline) {
            // Default suggestions and trending
            val defaultSuggestions = listOf(
                "Bangla new song",
                "Arijit Singh live",
                "Coke Studio Bangla",
                "Bangla natok 2026",
                "Android Jetpack Compose"
            )
            Text(
                text = "Trending searches",
                color = theme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            defaultSuggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { triggerSearch(suggestion) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, null, tint = theme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(suggestion, color = theme.onBackground, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Popular Videos",
                color = theme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(videos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onVideoClick(video) },
                        onSaveWatchLater = { onSaveWatchLater(video) },
                        onDownload = { onDownload(video) },
                        onShare = { onShare(video) },
                        onChannelClick = { onChannelClick(video.channelId) }
                    )
                }
            }
        } else if (!isSearchingOnline) {
            // Search Results List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Found ${displayVideos.size} real YouTube results for \"$searchQuery\"",
                        color = theme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(displayVideos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onClick = { onVideoClick(video) },
                        onSaveWatchLater = { onSaveWatchLater(video) },
                        onDownload = { onDownload(video) },
                        onShare = { onShare(video) },
                        onChannelClick = { onChannelClick(video.channelId) }
                    )
                }
            }
        }
    }
}
