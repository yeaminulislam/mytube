package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Returns true when the video is a real YouTube video (watch URL or 11-char YouTube id)
 * and therefore must be played through the WebView/YouTube IFrame engine,
 * while plain MP4/HLS links can be played with the native MediaPlayer.
 */
fun isYouTubeVideo(video: VideoItem): Boolean =
    video.videoUrl.contains("youtube.com", ignoreCase = true) ||
        video.videoUrl.contains("youtu.be", ignoreCase = true) ||
        video.id.length == 11

data class Chapter(
    val title: String,
    val startSecond: Int
)

data class SubtitleLine(
    val language: String,
    val startSecond: Int,
    val endSecond: Int,
    val text: String
)

data class ChannelItem(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val subscriberCount: String,
    val bannerUrl: String? = null
)

data class VideoQuality(
    val label: String,      // e.g. "1080p60 Premium", "1080p", "720p", "480p", "360p", "240p", "144p", "Auto"
    val bitrateKbps: Int,
    val isPremium: Boolean = false
)

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelName: String,
    val channelAvatarUrl: String,
    val subscriberCount: String,
    val thumbnailUrl: String,
    val videoUrl: String, // HLS / MP4 stream link
    val durationSeconds: Int,
    val viewCount: Long,
    val uploadDateText: String,
    val likesCount: Long,
    val dislikesCount: Long,
    val category: String, // "Coding", "Music", "Gaming", "News", "Tech", "Podcasts"
    val isLive: Boolean = false,
    val isShort: Boolean = false,
    val ambientColor: Color = Color(0xFFE50914),
    val chapters: List<Chapter> = emptyList(),
    val subtitles: List<SubtitleLine> = emptyList(),
    val availableQualities: List<VideoQuality> = listOf(
        VideoQuality("Auto (1080p)", 4500),
        VideoQuality("1080p Premium (High Bitrate)", 8500, isPremium = true),
        VideoQuality("1080p60", 4500),
        VideoQuality("720p", 2500),
        VideoQuality("480p", 1200),
        VideoQuality("360p", 700),
        VideoQuality("144p", 250)
    )
)

data class ShortItem(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorAvatar: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val likesCount: Long,
    val commentsCount: Long,
    val soundTrackTitle: String,
    val isSubscribed: Boolean = false
)

data class CommentItem(
    val id: String,
    val videoId: String,
    val authorName: String,
    val authorAvatar: String,
    val text: String,
    val timeAgo: String,
    val likesCount: Long,
    val isPinned: Boolean = false,
    val hasCreatorHeart: Boolean = false,
    val timestampSecond: Int? = null,
    val replies: List<CommentItem> = emptyList()
)

data class CommunityPost(
    val id: String,
    val channelName: String,
    val channelAvatar: String,
    val timeAgo: String,
    val contentText: String,
    val pollQuestion: String? = null,
    val pollOptions: List<PollOption> = emptyList(),
    val likesCount: Long,
    val commentsCount: Long,
    val isMemberOnly: Boolean = false
)

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int,
    val userVoted: Boolean = false
)

data class LiveChatMessage(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val message: String,
    val isModerator: Boolean = false,
    val isSuperChat: Boolean = false,
    val superChatAmount: String? = null,
    val superChatColor: Color? = null
)

data class GoogleAccount(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String,
    val channelHandle: String,
    val isPremium: Boolean = true
)
