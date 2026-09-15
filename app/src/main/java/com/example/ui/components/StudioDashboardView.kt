package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.CommunityPost
import com.example.model.GoogleAccount
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed

@Composable
fun StudioDashboardDialog(
    currentAccount: GoogleAccount?,
    onOpenUpload: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val votedPollOptions = remember { mutableStateMapOf<String, Int>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF242424),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("studio_dashboard_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = YouTubeRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "YouTube Studio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tabs: Analytics, Community Posts
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF2E2E2E),
                    contentColor = YouTubeRed
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Analytics", fontWeight = FontWeight.SemiBold, color = if (selectedTab == 0) YouTubeRed else Color.LightGray) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Community & Polls", fontWeight = FontWeight.SemiBold, color = if (selectedTab == 1) YouTubeRed else Color.LightGray) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Analytics Overview
                    Text("Channel Analytics (Last 28 Days)", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(title = "Views", value = "124.5K", delta = "+18%", modifier = Modifier.weight(1f))
                        StatCard(title = "Watch Time", value = "4.2K hrs", delta = "+12%", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(title = "Subscribers", value = "+890", delta = "+34%", modifier = Modifier.weight(1f))
                        StatCard(title = "Est. Revenue", value = "$648.20", delta = "+8.4%", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onDismiss()
                            onOpenUpload()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload New Video", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Community Posts & Interactive Polls
                    SampleData.sampleCommunityPosts.forEach { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = YouTubeDarkCard)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = post.channelAvatar,
                                        contentDescription = post.channelName,
                                        modifier = Modifier.size(32.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(post.channelName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(post.timeAgo, color = Color(0xFFAAAAAA), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(post.contentText, color = Color.White, fontSize = 13.sp)

                                // Poll options
                                if (post.pollOptions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val totalVotes = post.pollOptions.sumOf { it.votes } + if (votedPollOptions.containsKey(post.id)) 1 else 0
                                    post.pollOptions.forEachIndexed { optIndex, pollOption ->
                                        val hasVoted = votedPollOptions.containsKey(post.id)
                                        val isThisOptionVoted = votedPollOptions[post.id] == optIndex
                                        val percentage = if (optIndex == 0) 58 else if (optIndex == 1) 28 else 14

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isThisOptionVoted) YouTubeRed.copy(alpha = 0.25f) else Color(0xFF333333))
                                                .clickable { votedPollOptions[post.id] = optIndex }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(pollOption.text, color = Color.White, fontSize = 13.sp, fontWeight = if (isThisOptionVoted) FontWeight.Bold else FontWeight.Normal)
                                                if (hasVoted) {
                                                    Text("$percentage%", color = if (isThisOptionVoted) YouTubeRed else Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            if (hasVoted) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                LinearProgressIndicator(
                                                    progress = { percentage / 100f },
                                                    color = if (isThisOptionVoted) YouTubeRed else Color(0xFF666666),
                                                    trackColor = Color(0xFF444444),
                                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                                )
                                            }
                                        }
                                    }
                                    Text("$totalVotes votes", color = Color(0xFFAAAAAA), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    delta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = YouTubeDarkCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color(0xFFAAAAAA), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(delta, color = Color(0xFF2BA640), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
