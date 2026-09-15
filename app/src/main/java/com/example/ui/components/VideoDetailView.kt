package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.data.repository.BellNotificationMode
import com.example.model.CommentItem
import com.example.model.GoogleAccount
import com.example.model.VideoItem
import com.example.player.PlayerState
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed

@Composable
fun VideoDetailView(
    video: VideoItem,
    playerState: PlayerState,
    currentAccount: GoogleAccount?,
    isLiked: Boolean,
    isDisliked: Boolean,
    isSubscribed: Boolean,
    bellMode: BellNotificationMode,
    isDownloaded: Boolean,
    isWatchLater: Boolean,
    onLikeToggle: () -> Unit,
    onDislikeToggle: () -> Unit,
    onSubscribeToggle: () -> Unit,
    onBellModeChange: (BellNotificationMode) -> Unit,
    onDownloadClick: (VideoItem) -> Unit,
    onWatchLaterToggle: (VideoItem) -> Unit,
    onOpenComments: () -> Unit,
    onChannelClick: () -> Unit,
    onSelectRecommendedVideo: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showBellMenu by remember { mutableStateOf(false) }
    var showMembershipDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(14.dp)
            .testTag("video_detail_section")
    ) {
        // Video Title
        Text(
            text = video.title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Metadata & Expandable Description Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(YouTubeDarkCard)
                .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${formatViews(video.viewCount)} views  •  ${video.uploadDateText}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "#${video.category}",
                    color = Color(0xFF3EA6FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = video.description,
                color = Color(0xFFCCCCCC),
                fontSize = 12.sp,
                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Text(
                text = if (isDescriptionExpanded) "Show less" else "...more",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Live Chat for LIVE streams
        if (video.isLive) {
            Spacer(modifier = Modifier.height(14.dp))
            LiveStreamChatView(currentAccount = currentAccount)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Channel Info & Subscribe Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onChannelClick)
            ) {
                AsyncImage(
                    model = video.channelAvatarUrl,
                    contentDescription = video.channelName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = video.channelName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${video.subscriberCount} subscribers",
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Join Membership Button
                OutlinedButton(
                    onClick = { showMembershipDialog = true },
                    modifier = Modifier
                        .height(34.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(17.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3EA6FF)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF3EA6FF))
                ) {
                    Text("Join", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Subscribe Button & Bell
                Button(
                    onClick = onSubscribeToggle,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color(0xFF2E2E2E) else Color.White,
                        contentColor = if (isSubscribed) Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = if (isSubscribed) "Subscribed" else "Subscribe",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSubscribed) {
                    Box {
                        IconButton(
                            onClick = { showBellMenu = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = when (bellMode) {
                                    BellNotificationMode.ALL -> Icons.Default.NotificationsActive
                                    BellNotificationMode.PERSONALIZED -> Icons.Default.Notifications
                                    BellNotificationMode.NONE -> Icons.Default.NotificationsNone
                                },
                                contentDescription = "Notification Settings",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showBellMenu,
                            onDismissRequest = { showBellMenu = false },
                            modifier = Modifier.background(Color(0xFF282828))
                        ) {
                            DropdownMenuItem(
                                text = { Text("All notifications", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.NotificationsActive, null, tint = Color.White) },
                                onClick = {
                                    onBellModeChange(BellNotificationMode.ALL)
                                    showBellMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Personalized", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Notifications, null, tint = Color.White) },
                                onClick = {
                                    onBellModeChange(BellNotificationMode.PERSONALIZED)
                                    showBellMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("None", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.NotificationsNone, null, tint = Color.White) },
                                onClick = {
                                    onBellModeChange(BellNotificationMode.NONE)
                                    showBellMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Action Ribbon: Like/Dislike, Share, Download, Watch Later, Clip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like & Dislike combined pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(YouTubeDarkCard)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onLikeToggle)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = "Like",
                        tint = if (isLiked) YouTubeRed else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val likesDisplay = if (isLiked) video.likesCount + 1 else video.likesCount
                    Text(
                        text = formatViews(likesDisplay),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                IconButton(
                    onClick = onDislikeToggle,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                        contentDescription = "Dislike",
                        tint = if (isDisliked) Color(0xFF3EA6FF) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Share Button (generates URL with timestamp)
            ActionPill(
                icon = Icons.Default.Share,
                text = "Share",
                onClick = {
                    val currentSec = playerState.currentPositionMs / 1000
                    val shareUrl = "https://youtu.be/${video.id}?t=${currentSec}s"
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("YouTube Link", shareUrl))
                    Toast.makeText(context, "Link with timestamp copied: $shareUrl", Toast.LENGTH_SHORT).show()
                }
            )

            // Download (Offline) Button
            ActionPill(
                icon = if (isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                text = if (isDownloaded) "Downloaded" else "Download",
                tint = if (isDownloaded) YouTubePremiumGold else Color.White,
                onClick = { onDownloadClick(video) }
            )

            // Watch Later Button
            ActionPill(
                icon = Icons.Default.WatchLater,
                text = if (isWatchLater) "Saved" else "Watch Later",
                tint = if (isWatchLater) Color(0xFF3EA6FF) else Color.White,
                onClick = { onWatchLaterToggle(video) }
            )

            // Remix / Clip
            ActionPill(
                icon = Icons.Default.ContentCut,
                text = "Clip",
                onClick = {
                    Toast.makeText(context, "Clip generated at current timestamp", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments Preview Section
        val comments = SampleData.sampleComments.filter { it.videoId == video.id }.ifEmpty { SampleData.sampleComments }
        val pinnedComment = comments.firstOrNull { it.isPinned } ?: comments.firstOrNull()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(YouTubeDarkCard)
                .clickable(onClick = onOpenComments)
                .padding(12.dp)
                .testTag("comments_preview_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments • ${comments.size + 142}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "View all",
                    color = Color(0xFF3EA6FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (pinnedComment != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    AsyncImage(
                        model = pinnedComment.authorAvatar,
                        contentDescription = pinnedComment.authorName,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = pinnedComment.authorName,
                                color = Color(0xFFAAAAAA),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (pinnedComment.hasCreatorHeart) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Creator Heart",
                                    tint = YouTubeRed,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = pinnedComment.text,
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Next Up / Recommended Videos Header
        Text(
            text = "Up next",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Recommended Videos
        val recommended = SampleData.sampleVideos.filter { it.id != video.id }
        recommended.forEach { recVideo ->
            VideoCard(
                video = recVideo,
                onClick = { onSelectRecommendedVideo(recVideo) },
                // Each recommended card acts on ITS OWN video (not the current one)
                onSaveWatchLater = { onWatchLaterToggle(recVideo) },
                onDownload = { onDownloadClick(recVideo) },
                onShare = {},
                onChannelClick = onChannelClick
            )
        }
    }

    // Membership dialog
    if (showMembershipDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showMembershipDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF282828),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, null, tint = YouTubePremiumGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Join ${video.channelName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "• Exclusive loyalty badges\n• Custom member emojis in live chat\n• Members-only community posts & early video access",
                        color = Color(0xFFCCCCCC),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Joined ${video.channelName} Membership!", Toast.LENGTH_SHORT).show()
                            showMembershipDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3EA6FF))
                    ) {
                        Text("Join for $4.99/month", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(YouTubeDarkCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = tint, modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = tint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatViews(views: Long): String {
    return when {
        views >= 1_000_000 -> String.format("%.1fM", views / 1_000_000.0)
        views >= 1_000 -> String.format("%.1fK", views / 1_000.0)
        else -> views.toString()
    }
}
