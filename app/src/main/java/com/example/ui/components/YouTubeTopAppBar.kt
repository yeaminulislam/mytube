package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.GoogleAccount
import com.example.ui.theme.LocalAppTheme

@Composable
fun YouTubeTopAppBar(
    currentAccount: GoogleAccount?,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCastClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onThemeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(theme.background)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Brand
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("yt_header_logo")
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_yt_banner_logo),
                contentDescription = "YouTube Logo",
                modifier = Modifier.height(22.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "YouTube",
                color = theme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                letterSpacing = (-0.5).sp
            )
            if (currentAccount?.isPremium == true) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.card)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PREMIUM",
                        color = theme.onSurfaceVariant,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Action Icons: Theme Selector, Cast, Notifications, Search, User Google Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Theme Picker Icon Button
            IconButton(
                onClick = onThemeClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("theme_picker_button")
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Change Theme",
                        tint = theme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                    // Theme color indicator dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(theme.primary)
                    )
                }
            }

            IconButton(
                onClick = onCastClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "Cast to TV",
                    tint = theme.onBackground
                )
            }

            BadgedBox(
                badge = {
                    Badge(
                        containerColor = theme.primary,
                        contentColor = if (theme.isDark) Color.Black else Color.White
                    ) {
                        Text("3", fontSize = 10.sp)
                    }
                }
            ) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = theme.onBackground
                    )
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("search_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = theme.onBackground
                )
            }

            // User Google Avatar or Sign-In icon
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick)
                    .testTag("user_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                if (currentAccount != null && currentAccount.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = currentAccount.avatarUrl,
                        contentDescription = "Profile: ${currentAccount.displayName}",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, theme.border, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Sign in",
                        tint = theme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
