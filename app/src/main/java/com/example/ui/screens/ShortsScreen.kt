package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.model.GoogleAccount
import com.example.model.ShortItem
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ShortsItemView

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
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val short = shorts[page]
            ShortsItemView(
                short = short,
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
