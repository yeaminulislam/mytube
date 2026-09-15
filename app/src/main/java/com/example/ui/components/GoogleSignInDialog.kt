package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.SampleData
import com.example.model.GoogleAccount
import com.example.ui.theme.GoogleButtonBorder
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubePremiumGold
import com.example.ui.theme.YouTubeRed

@Composable
fun GoogleSignInDialog(
    currentAccount: GoogleAccount?,
    onSelectAccount: (GoogleAccount) -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var showCustomInput by remember { mutableStateOf(false) }
    var userNameInput by remember { mutableStateOf("") }
    var userEmailInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF242424),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("google_sign_in_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Google Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Google Account",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current account status banner
                if (currentAccount != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1B)),
                        border = BorderStroke(1.dp, YouTubeRed.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "বর্তমানে সাইন ইন করা আছে:",
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentAccount.avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = currentAccount.avatarUrl,
                                        contentDescription = currentAccount.displayName,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentAccount.displayName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = currentAccount.email,
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sign Out All Button
                            Button(
                                onClick = {
                                    onSignOut()
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সমস্ত জিমেইল লগ আউট করুন (Log Out)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "অথবা অন্য কোনো জিমেইল দিয়ে সাইন ইন করুন:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "কোনো অ্যাকাউন্ট সাইন ইন করা নেই",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "ভিডিও ওয়াচ হিস্ট্রি ও সাবস্ক্রিপশন সিঙ্ক করতে সাইন ইন করুন।",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!showCustomInput) {
                    // Quick Google Sign-In with Demo / New
                    OutlinedButton(
                        onClick = {
                            val acc = GoogleAccount(
                                id = "g_user_${System.currentTimeMillis() % 10000}",
                                displayName = "My Google Account",
                                email = "user@gmail.com",
                                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop",
                                channelHandle = "@my_channel",
                                isPremium = true
                            )
                            onSelectAccount(acc)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("continue_with_google_btn"),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, GoogleButtonBorder),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Google",
                                color = Color(0xFF1F1F1F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showCustomInput = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(21.dp),
                        border = BorderStroke(1.dp, Color(0xFF444444)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(Icons.Default.PersonAdd, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "নিজের নাম ও জিমেইল দিয়ে সাইন ইন",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // Custom Gmail / Name Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            label = { Text("আপনার নাম (Name)", color = Color.Gray) },
                            placeholder = { Text("যেমন: Yeamin / User", color = Color.DarkGray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF181818),
                                unfocusedContainerColor = Color(0xFF181818)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userEmailInput,
                            onValueChange = { userEmailInput = it },
                            label = { Text("জিমেইল অ্যাড্রেস (Gmail)", color = Color.Gray) },
                            placeholder = { Text("example@gmail.com", color = Color.DarkGray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF181818),
                                unfocusedContainerColor = Color(0xFF181818)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { showCustomInput = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("বাতিল", color = Color.LightGray)
                            }

                            Button(
                                onClick = {
                                    val name = userNameInput.ifBlank { "Google User" }
                                    val email = userEmailInput.ifBlank { "user@gmail.com" }
                                    val handle = "@" + name.lowercase().replace(" ", "_")
                                    val avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop"
                                    val newAcc = GoogleAccount(
                                        id = "custom_${System.currentTimeMillis()}",
                                        displayName = name,
                                        email = email,
                                        avatarUrl = avatar,
                                        channelHandle = handle,
                                        isPremium = false
                                    )
                                    onSelectAccount(newAcc)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("সাইন ইন করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        onSignOut()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "সাইন ইন ছাড়া ব্যবহার করুন (Guest Mode)",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

