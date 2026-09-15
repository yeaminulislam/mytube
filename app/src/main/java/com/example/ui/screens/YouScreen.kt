package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.CustomPlaylistEntity
import com.example.data.local.OfflineVideoEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.local.WatchLaterEntity
import com.example.model.GoogleAccount
import com.example.model.VideoItem
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.AppThemePresets
import com.example.ui.theme.GoogleButtonBorder
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed

@Composable
fun YouScreen(
    currentAccount: GoogleAccount?,
    historyVideos: List<WatchHistoryEntity>,
    downloads: List<OfflineVideoEntity>,
    watchLaterList: List<WatchLaterEntity>,
    playlists: List<CustomPlaylistEntity>,
    isAmbientMode: Boolean,
    isBackgroundPlayback: Boolean,
    isRestrictedMode: Boolean,
    currentTheme: AppThemePreset = AppThemePresets.GreenBlack,
    onSelectTheme: (AppThemePreset) -> Unit = {},
    onOpenThemeDialog: () -> Unit = {},
    onOpenGoogleSignIn: () -> Unit,
    onSignOut: () -> Unit = {},
    onOpenTimeWatched: () -> Unit,
    onOpenStudio: () -> Unit,
    onClearHistory: () -> Unit,
    onDeleteDownload: (String) -> Unit,
    onToggleAmbientMode: () -> Unit,
    onToggleBackgroundPlayback: () -> Unit,
    onToggleRestrictedMode: () -> Unit,
    onPlayHistoryVideo: (WatchHistoryEntity) -> Unit,
    onPlayOfflineVideo: (OfflineVideoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(14.dp)
            .testTag("you_screen")
    ) {
        // App Theme & Colors Customization Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("theme_selector_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.card),
                border = BorderStroke(1.dp, theme.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = theme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "থিম ও রঙের স্টাইল (Themes)",
                                    color = theme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "বর্তমান: ${currentTheme.nameBn}",
                                    color = theme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            text = "সব থিম",
                            color = theme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenThemeDialog)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "যেকোনো মিক্সিং রঙ সিলেক্ট করুন (যেমন সবুজ-কালো, লাল-কালো, সায়ান):",
                        color = theme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Horizontal Presets Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AppThemePresets.allPresets) { preset ->
                            val isSelected = preset.id == currentTheme.id
                            Card(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectTheme(preset) }
                                    .testTag("quick_theme_${preset.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) preset.surface else theme.surface
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) preset.primary else theme.border
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(preset.background)
                                            .border(1.5.dp, preset.border, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(preset.primary)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = preset.nameBn.substringBefore(" ("),
                                            color = if (preset.isDark) Color.White else Color.Black,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        Text(
                                            text = preset.nameEn.substringBefore(" &"),
                                            color = if (isSelected) preset.primary else theme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // User Profile & Google Account Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = theme.card)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar: ONLY show image if user is signed in with a valid avatar; otherwise show neutral account icon!
                        if (currentAccount != null && currentAccount.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = currentAccount.avatarUrl,
                                contentDescription = currentAccount.displayName,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, theme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(theme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Guest",
                                    tint = theme.onSurfaceVariant,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentAccount?.displayName ?: "সাইন ইন করা নেই (Guest)",
                                    color = theme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (currentAccount?.isPremium == true) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Premium",
                                        tint = YouTubePremiumGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = currentAccount?.email ?: "ভিডিও লাইক ও সাবস্ক্রাইব করতে সাইন ইন করুন",
                                color = theme.onSurfaceVariant,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (currentAccount != null) {
                                Text(
                                    text = currentAccount.channelHandle,
                                    color = theme.primary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominent Google Sign-In or Sign-Out Buttons
                    if (currentAccount == null) {
                        OutlinedButton(
                            onClick = onOpenGoogleSignIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("google_login_action_btn"),
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(1.dp, GoogleButtonBorder),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "গুগল একাউন্ট দিয়ে সাইন ইন করুন",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onOpenGoogleSignIn,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, theme.border),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = theme.surface)
                            ) {
                                Icon(Icons.Default.SwitchAccount, null, tint = theme.onBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("অ্যাকাউন্ট বদলান", color = theme.onBackground, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onSignOut,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = YouTubeRed.copy(alpha = 0.1f))
                            ) {
                                Icon(Icons.Default.Logout, null, tint = YouTubeRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("লগ আউট", color = YouTubeRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // History Section (Room DB persistence)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                if (historyVideos.isNotEmpty()) {
                    Text(
                        text = "Clear history",
                        color = Color(0xFF3EA6FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onClearHistory() }
                    )
                }
            }

            if (historyVideos.isEmpty()) {
                Text("No videos watched yet", color = Color(0xFFAAAAAA), fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(historyVideos, key = { it.videoId }) { item ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onPlayHistoryVideo(item) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = YouTubeDarkCard)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                ) {
                                    AsyncImage(
                                        model = item.thumbnailUrl,
                                        contentDescription = item.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Watch progress bar
                                    val frac = if (item.durationSeconds > 0) (item.watchedPositionSeconds.toFloat() / item.durationSeconds).coerceIn(0f, 1f) else 0.4f
                                    LinearProgressIndicator(
                                        progress = { frac },
                                        color = YouTubeRed,
                                        trackColor = Color.DarkGray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .align(Alignment.BottomCenter)
                                    )
                                }
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Offline Downloads Section (Room DB)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, null, tint = YouTubePremiumGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Downloads (Offline)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("${downloads.size} videos", color = Color(0xFFAAAAAA), fontSize = 12.sp)
            }

            if (downloads.isEmpty()) {
                Text(
                    text = "No downloaded videos. Tap 'Download' on any video to watch offline without internet.",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                downloads.forEach { dl ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(YouTubeDarkCard)
                            .clickable { onPlayOfflineVideo(dl) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = dl.thumbnailUrl,
                            contentDescription = dl.title,
                            modifier = Modifier
                                .size(80.dp, 48.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dl.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${dl.channelName} • ${dl.quality} (${dl.fileSizeMb}MB)", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                        }
                        IconButton(onClick = { onDeleteDownload(dl.videoId) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete download", tint = Color.LightGray)
                        }
                    }
                }
            }
        }

        // Quick Hub Links: Time Watched, Studio, Playlists
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Management & Creator Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Time Watched
            HubRow(
                icon = Icons.Default.QueryBuilder,
                title = "Time watched & Wellbeing",
                subtitle = "Daily stats, bedtime and break reminders",
                onClick = onOpenTimeWatched
            )

            // YouTube Studio
            HubRow(
                icon = Icons.Default.Analytics,
                title = "YouTube Studio",
                subtitle = "Analytics, subscriber growth, community polls",
                onClick = onOpenStudio
            )
        }

        // Settings & Switches (Ambient mode, Background playback, Restricted mode)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Settings & Playback Preferences", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Ambient Mode Switch
            SettingToggleRow(
                title = "Ambient Mode",
                subtitle = "Dynamic color glow around video player",
                checked = isAmbientMode,
                onCheckedChange = { onToggleAmbientMode() }
            )

            // Background Playback Switch
            SettingToggleRow(
                title = "Background Audio Playback",
                subtitle = "Keep audio playing when app is minimized or screen is off",
                checked = isBackgroundPlayback,
                onCheckedChange = { onToggleBackgroundPlayback() }
            )

            // Restricted Mode Switch
            SettingToggleRow(
                title = "Restricted Mode",
                subtitle = "Hide potentially mature videos",
                checked = isRestrictedMode,
                onCheckedChange = { onToggleRestrictedMode() }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun HubRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val theme = LocalAppTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = theme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = theme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = theme.onSurfaceVariant, fontSize = 11.sp)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = theme.onSurfaceVariant.copy(alpha = 0.6f))
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val theme = LocalAppTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = theme.onBackground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = theme.onSurfaceVariant, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = if (theme.isDark) Color.White else Color.Black,
                checkedTrackColor = theme.primary
            )
        )
    }
}
