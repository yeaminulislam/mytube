package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.model.GoogleAccount
import com.example.model.ShortItem
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ShortsItemView
import com.example.ui.theme.YouTubeRed

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    shorts: List<ShortItem>,
    currentAccount: GoogleAccount?,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { shorts.size })
    var activeCommentsShortId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_screen")
    ) {
        if (shorts.isEmpty()) {
            // Empty state instead of crashing on an empty pager
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = YouTubeRed,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("কোনো Short খুঁজে পাওয়া যায়নি", color = Color.White, fontSize = 14.sp)
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val short = shorts[page]
                ShortsItemView(
                    short = short,
                    isActive = page == pagerState.currentPage,
                    onOpenComments = { activeCommentsShortId = short.id }
                )
            }

            activeCommentsShortId?.let { shortId ->
                CommentsBottomSheet(
                    videoId = shortId,
                    currentAccount = currentAccount,
                    onTimestampClick = {},
                    onDismiss = { activeCommentsShortId = null }
                )
            }
        }
    }
}
