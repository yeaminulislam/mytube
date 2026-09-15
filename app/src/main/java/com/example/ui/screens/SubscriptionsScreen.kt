package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.data.repository.BellNotificationMode
import com.example.model.VideoItem
import com.example.ui.components.VideoCard
import com.example.ui.theme.LocalAppTheme

@Composable
fun SubscriptionsScreen(
    videos: List<VideoItem>,
    subscribedMap: Map<String, BellNotificationMode>,
    onVideoClick: (VideoItem) -> Unit,
    onSaveWatchLater: (VideoItem) -> Unit,
    onDownload: (VideoItem) -> Unit,
    onShare: (VideoItem) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val channels = SampleData.sampleChannels
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .testTag("subscriptions_screen")
    ) {
        // Subscribed Channels Story Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onChannelClick(channel.id) }
                        .width(62.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(2.dp, theme.primary, CircleShape)
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = channel.avatarUrl,
                            contentDescription = channel.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        // Blue unread upload indicator dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3EA6FF))
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Subscriptions Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Today", "Live", "Unwatched", "Settings").forEach { f ->
                val isSelected = selectedFilter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) theme.primary else theme.card)
                        .clickable { selectedFilter = f }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = f,
                        color = if (isSelected) (if (theme.isDark) Color.Black else Color.White) else theme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Subscriptions Feed
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
    }
}
