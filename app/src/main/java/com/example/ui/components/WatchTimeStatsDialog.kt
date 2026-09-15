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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubeRed

@Composable
fun WatchTimeStatsDialog(
    isRestrictedMode: Boolean,
    onToggleRestrictedMode: () -> Unit,
    onDismiss: () -> Unit
) {
    var isBreakReminderOn by remember { mutableStateOf(true) }
    var isBedtimeReminderOn by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF262626),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("watch_time_stats_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QueryBuilder, contentDescription = null, tint = YouTubeRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Time watched & Wellbeing",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(YouTubeDarkCard)
                        .padding(14.dp)
                ) {
                    Text("Today's Watch Time", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("1 hr 45 min", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        color = YouTubeRed,
                        trackColor = Color(0xFF444444),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Daily Average", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                            Text("2 hr 10 min", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Past 7 Days", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                            Text("15 hr 12 min", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text("Tools to manage your YouTube time", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Remind me to take a break
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Timer, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Remind me to take a break", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Every 45 minutes of continuous watching", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isBreakReminderOn,
                        onCheckedChange = { isBreakReminderOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = YouTubeRed)
                    )
                }

                // Bedtime reminder
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Bedtime, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Remind me when it's bedtime", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("11:00 PM – 7:00 AM", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isBedtimeReminderOn,
                        onCheckedChange = { isBedtimeReminderOn = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = YouTubeRed)
                    )
                }

                // Restricted Mode Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Lock, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Restricted Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Hide potentially mature videos & filtered comments", color = Color(0xFFAAAAAA), fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isRestrictedMode,
                        onCheckedChange = { onToggleRestrictedMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = YouTubeRed)
                    )
                }
            }
        }
    }
}
