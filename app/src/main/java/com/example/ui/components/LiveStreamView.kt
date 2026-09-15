package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.GoogleAccount
import com.example.model.LiveChatMessage
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed
import kotlinx.coroutines.delay

@Composable
fun LiveStreamChatView(
    currentAccount: GoogleAccount?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<LiveChatMessage>().apply { addAll(SampleData.sampleLiveMessages) } }
    var inputChat by remember { mutableStateOf("") }
    var showSuperChatDialog by remember { mutableStateOf(false) }

    // Simulate real-time chat updates
    LaunchedEffect(Unit) {
        val incomingChatters = listOf(
            Pair("KotlinNinja", "Awesome stream! The latency is under 1 second."),
            Pair("MobilePro", "Hello from California!"),
            Pair("Alex_99", "Loving the new Android 16 features discussed here."),
            Pair("GamingKing", "Let's goooo! 🔥🔥🔥")
        )
        var idx = 0
        while (true) {
            delay(5000)
            val chatter = incomingChatters[idx % incomingChatters.size]
            messages.add(
                LiveChatMessage(
                    id = "live_${System.currentTimeMillis()}",
                    authorName = chatter.first,
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    message = chatter.second
                )
            )
            idx++
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFF181818))
            .padding(8.dp)
            .testTag("live_chat_container")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(YouTubeRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Live Chat",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• 12.4K watching",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp
                )
            }

            // Super Chat button
            IconButton(
                onClick = { showSuperChatDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Super Chat",
                    tint = YouTubePremiumGold
                )
            }
        }

        // Chat messages stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = false
        ) {
            items(messages, key = { it.id }) { msg ->
                if (msg.isSuperChat) {
                    // Super Chat Banner Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(msg.superChatColor ?: YouTubePremiumGold)
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = msg.authorAvatar,
                                    contentDescription = msg.authorName,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg.authorName,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = msg.superChatAmount ?: "$10.00",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // Regular Chat Message
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AsyncImage(
                            model = msg.authorAvatar,
                            contentDescription = msg.authorName,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (msg.isModerator) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Mod",
                                tint = Color(0xFF3EA6FF),
                                modifier = Modifier
                                    .size(13.dp)
                                    .padding(end = 2.dp)
                            )
                        }
                        Text(
                            text = "${msg.authorName}: ",
                            color = if (msg.isModerator) Color(0xFF3EA6FF) else Color(0xFFAAAAAA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = msg.message,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Live Chat input box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputChat,
                onValueChange = { inputChat = it },
                placeholder = { Text("Chat as ${currentAccount?.displayName ?: "Guest"}...", color = Color.Gray, fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF262626),
                    unfocusedContainerColor = Color(0xFF262626),
                    focusedBorderColor = YouTubeRed,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (inputChat.isNotBlank()) {
                        messages.add(
                            LiveChatMessage(
                                id = "user_msg_${System.currentTimeMillis()}",
                                authorName = currentAccount?.displayName ?: "You",
                                authorAvatar = currentAccount?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                message = inputChat
                            )
                        )
                        inputChat = ""
                    }
                },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Chat",
                    tint = if (inputChat.isNotBlank()) YouTubeRed else Color.Gray
                )
            }
        }
    }

    // Super Chat Dialog
    if (showSuperChatDialog) {
        Dialog(onDismissRequest = { showSuperChatDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF242424),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                var donationAmount by remember { mutableStateOf("$5.00") }
                var donationMessage by remember { mutableStateOf("Great stream! Support from Bangladesh! 🇧🇩") }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Send a Super Chat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { showSuperChatDialog = false }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("$2.00", "$5.00", "$10.00", "$50.00").forEach { amt ->
                            val isSelected = donationAmount == amt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) YouTubePremiumGold else Color(0xFF333333))
                                    .clickable { donationAmount = amt }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = amt,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = donationMessage,
                        onValueChange = { donationMessage = it },
                        label = { Text("Your Message", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            messages.add(
                                LiveChatMessage(
                                    id = "sc_${System.currentTimeMillis()}",
                                    authorName = currentAccount?.displayName ?: "You",
                                    authorAvatar = currentAccount?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                    message = donationMessage,
                                    isSuperChat = true,
                                    superChatAmount = donationAmount,
                                    superChatColor = if (donationAmount == "$50.00") YouTubePremiumGold else Color(0xFF00B0FF)
                                )
                            )
                            Toast.makeText(context, "Sent $donationAmount Super Chat!", Toast.LENGTH_SHORT).show()
                            showSuperChatDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                    ) {
                        Text("Buy and Send $donationAmount", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
