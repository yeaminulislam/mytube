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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.CommentItem
import com.example.model.GoogleAccount
import com.example.ui.theme.YouTubeDarkCard
import com.example.ui.theme.YouTubeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    videoId: String,
    currentAccount: GoogleAccount?,
    onTimestampClick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val comments = remember {
        mutableStateListOf<CommentItem>().apply {
            addAll(SampleData.sampleComments.filter { it.videoId == videoId }.ifEmpty { SampleData.sampleComments })
        }
    }
    var newCommentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF212121),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("comments_bottom_sheet")
        ) {
            // Sheet Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 16.dp)
            ) {
                items(comments, key = { it.id }) { comment ->
                    CommentRow(
                        comment = comment,
                        onTimestampClick = onTimestampClick
                    )
                }
            }

            // Comment Input Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentAccount?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Add a comment...", color = Color(0xFFAAAAAA), fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2C2C2C),
                        unfocusedContainerColor = Color(0xFF2C2C2C),
                        focusedBorderColor = YouTubeRed,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            comments.add(
                                0,
                                CommentItem(
                                    id = "comm_${System.currentTimeMillis()}",
                                    videoId = videoId,
                                    authorName = currentAccount?.displayName ?: "You",
                                    authorAvatar = currentAccount?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                                    text = newCommentText,
                                    timeAgo = "Just now",
                                    likesCount = 0
                                )
                            )
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Comment",
                        tint = if (newCommentText.isNotBlank()) YouTubeRed else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: CommentItem,
    onTimestampClick: (Int) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        if (comment.isPinned) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pinned by creator",
                    color = Color(0xFFAAAAAA),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = comment.authorAvatar,
                contentDescription = comment.authorName,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.authorName,
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = comment.timeAgo,
                        color = Color(0xFF777777),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = comment.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )

                // Timestamp jump link button if timestamp attached
                if (comment.timestampSecond != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E3A5F))
                            .clickable { onTimestampClick(comment.timestampSecond) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "▶ Jump to ${formatSeconds(comment.timestampSecond)}",
                            color = Color(0xFF3EA6FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Like, Reply, Creator Heart row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isLiked = !isLiked },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                            contentDescription = "Like",
                            tint = if (isLiked) YouTubeRed else Color.LightGray,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = (comment.likesCount + if (isLiked) 1 else 0).toString(),
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Reply",
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (comment.hasCreatorHeart) {
                        Spacer(modifier = Modifier.width(14.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Creator Heart",
                            tint = YouTubeRed,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Nested Replies if any
                if (comment.replies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "↳ ${comment.replies.size} reply",
                        color = Color(0xFF3EA6FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    comment.replies.forEach { reply ->
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            AsyncImage(
                                model = reply.authorAvatar,
                                contentDescription = reply.authorName,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${reply.authorName} • ${reply.timeAgo}",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = reply.text,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
