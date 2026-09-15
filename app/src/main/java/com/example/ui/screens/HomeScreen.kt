package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.network.YouTubeApiService
import com.example.model.GoogleAccount
import com.example.model.ShortItem
import com.example.model.VideoItem
import com.example.ui.components.VideoCard
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.YouTubePremiumGold

@Composable
fun HomeScreen(
    videos: List<VideoItem>,
    shorts: List<ShortItem>,
    currentAccount: GoogleAccount?,
    onVideoClick: (VideoItem) -> Unit,
    onShortClick: (ShortItem) -> Unit,
    onSaveWatchLater: (VideoItem) -> Unit,
    onDownload: (VideoItem) -> Unit,
    onShare: (VideoItem) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val categories = listOf("All", "Trending", "Bangla", "Music", "Gaming", "News", "Tech", "Coding")
    var selectedCategory by remember { mutableStateOf("All") }
    var categoryVideos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var isLoadingCategory by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != "All") {
            isLoadingCategory = true
            try {
                val fetched = YouTubeApiService.fetchCategoryVideos(selectedCategory)
                if (fetched.isNotEmpty()) {
                    categoryVideos = fetched
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingCategory = false
            }
        } else {
            categoryVideos = emptyList()
        }
    }

    val displayVideos = remember(videos, selectedCategory, categoryVideos) {
        if (categoryVideos.isNotEmpty()) {
            categoryVideos
        } else if (selectedCategory == "All") {
            videos
        } else {
            videos.filter { it.category.contains(selectedCategory, ignoreCase = true) || it.title.contains(selectedCategory, ignoreCase = true) }
                .ifEmpty { videos }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .testTag("home_screen")
    ) {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) theme.primary else theme.card)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) (if (theme.isDark) Color.Black else Color.White) else theme.onBackground,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        if (isLoadingCategory) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = theme.primary,
                trackColor = theme.card
            )
        }

        // Main Feed
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Premium Ad-Free Banner if user has Premium
            if (currentAccount?.isPremium == true) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2A2A2A), Color(0xFF1F1F1F))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = YouTubePremiumGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "YouTube Premium active: Background playback & zero ads enabled",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // First few videos
            items(displayVideos.take(2), key = { it.id }) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoClick(video) },
                    onSaveWatchLater = { onSaveWatchLater(video) },
                    onDownload = { onDownload(video) },
                    onShare = { onShare(video) },
                    onChannelClick = { onChannelClick(video.channelId) }
                )
            }

            // Shorts Shelf Section (Horizontal Carousel)
            if (shorts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Shorts",
                                tint = theme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shorts",
                                color = theme.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(shorts, key = { it.id }) { short ->
                                Card(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .aspectRatio(9f / 16f)
                                        .clickable { onShortClick(short) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = short.thumbnailUrl,
                                            contentDescription = short.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                                        startY = 150f
                                                    )
                                                )
                                        )
                                        Text(
                                            text = short.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Remaining Videos
            items(displayVideos.drop(2), key = { it.id }) { video ->
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
