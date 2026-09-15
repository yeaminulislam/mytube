package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.ChannelItem
import com.example.model.Chapter
import com.example.model.CommentItem
import com.example.model.CommunityPost
import com.example.model.GoogleAccount
import com.example.model.LiveChatMessage
import com.example.model.PollOption
import com.example.model.ShortItem
import com.example.model.SubtitleLine
import com.example.model.VideoItem

object SampleData {

    val sampleChannels = listOf(
        ChannelItem("chan_1", "Android Dev Master", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop", "1.42M"),
        ChannelItem("chan_2", "Google Developers", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop", "3.85M"),
        ChannelItem("chan_3", "GameVerse", "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150&auto=format&fit=crop", "890K"),
        ChannelItem("chan_4", "Chillhop Beats", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150&auto=format&fit=crop", "12.4M"),
        ChannelItem("chan_5", "Tech Pulse", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop", "2.1M")
    )

    val defaultGoogleAccounts = listOf(
        GoogleAccount(
            id = "g_acc_demo_1",
            displayName = "Demo User",
            email = "user@example.com",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop",
            channelHandle = "@demo_user",
            isPremium = true
        )
    )

    val sampleVideos = listOf(
        VideoItem(
            id = "vid_1",
            title = "Building a Full-Stack YouTube Clone in Android & Jetpack Compose",
            description = "In this comprehensive masterclass, learn how to architect and build a production-grade YouTube clone featuring HLS adaptive streaming, gesture controls, Ambient Mode, Picture-in-Picture, offline downloads, Shorts, and creator studio tooling.",
            channelId = "chan_1",
            channelName = "Android Dev Master",
            channelAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
            subscriberCount = "1.42M",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            durationSeconds = 596,
            viewCount = 384500,
            uploadDateText = "2 days ago",
            likesCount = 28400,
            dislikesCount = 190,
            category = "Coding",
            ambientColor = Color(0xFF007ACC),
            chapters = listOf(
                Chapter("00:00 Introduction & Overview", 0),
                Chapter("01:25 Architecture & Room DB", 85),
                Chapter("03:10 HLS Adaptive Video Player", 190),
                Chapter("05:40 Gesture Control & Ambient Mode", 340),
                Chapter("07:50 YouTube Premium & Downloads", 470),
                Chapter("09:15 Summary & Next Steps", 555)
            ),
            subtitles = listOf(
                SubtitleLine("English", 0, 5, "Welcome everyone to this YouTube Clone Architecture tutorial!"),
                SubtitleLine("English", 5, 12, "Today we cover video streaming, gesture controls and PiP."),
                SubtitleLine("English", 13, 20, "Let's first inspect the modern Android Jetpack Compose stack."),
                SubtitleLine("Bengali", 0, 5, "সবাইকে স্বাগতম আমাদের এই ইউটিউব ক্লোন আর্কিটেকচার টিউটোরিয়ালে!"),
                SubtitleLine("Bengali", 5, 12, "আজ আমরা অ্যাডাপ্টিভ স্ট্রিমিং ও গেস্টচার কন্ট্রোল নিয়ে আলোচনা করব।"),
                SubtitleLine("Spanish", 0, 5, "¡Bienvenidos a todos a este tutorial del clon de YouTube!")
            )
        ),
        VideoItem(
            id = "vid_2",
            title = "🔴 Google I/O Keynote: The Future of Android & Gemini AI",
            description = "Watch the live stream of the keynote unveiling next-generation AI agents, Android 16 features, and cutting-edge developer tools.",
            channelId = "chan_2",
            channelName = "Google Developers",
            channelAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop",
            subscriberCount = "3.85M",
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            durationSeconds = 1420,
            viewCount = 1250000,
            uploadDateText = "Streamed 4 hours ago",
            likesCount = 94300,
            dislikesCount = 820,
            category = "Live",
            isLive = true,
            ambientColor = Color(0xFFEA4335),
            chapters = listOf(
                Chapter("00:00 Opening Keynote", 0),
                Chapter("05:00 Gemini Models on Device", 300),
                Chapter("12:30 Jetpack Compose 2026", 750),
                Chapter("18:00 Closing Remarks", 1080)
            )
        ),
        VideoItem(
            id = "vid_3",
            title = "Cyberpunk 2077 Ultra 4K 60FPS Ray Tracing Gameplay",
            description = "Pure gameplay benchmark running on maximum settings with path tracing enabled. Experience Night City in true fidelity.",
            channelId = "chan_3",
            channelName = "GameVerse Benchmark",
            channelAvatarUrl = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150&auto=format&fit=crop",
            subscriberCount = "890K",
            thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            durationSeconds = 740,
            viewCount = 640000,
            uploadDateText = "1 week ago",
            likesCount = 42100,
            dislikesCount = 310,
            category = "Gaming",
            ambientColor = Color(0xFFFF5722),
            chapters = listOf(
                Chapter("00:00 Intro & City Drive", 0),
                Chapter("03:30 Heavy Combat Test", 210),
                Chapter("08:15 Benchmark Stats", 495)
            )
        ),
        VideoItem(
            id = "vid_4",
            title = "Lofi Hip Hop Radio - Beats to Relax / Study to 24/7",
            description = "Chill beats to relax, study, work or sleep to. Featuring talented lofi hip hop producers from around the world.",
            channelId = "chan_4",
            channelName = "Chillhop Beats",
            channelAvatarUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=150&auto=format&fit=crop",
            subscriberCount = "12.4M",
            thumbnailUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=800&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            durationSeconds = 1800,
            viewCount = 45120000,
            uploadDateText = "Live 24/7",
            likesCount = 3200000,
            dislikesCount = 14000,
            category = "Music",
            isLive = true,
            ambientColor = Color(0xFF9C27B0)
        ),
        VideoItem(
            id = "vid_5",
            title = "Top 10 Tech Inventions That Changed The World Forever",
            description = "From the semiconductor to quantum processors, explore the groundbreaking hardware breakthroughs in human history.",
            channelId = "chan_5",
            channelName = "Tech Pulse Official",
            channelAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop",
            subscriberCount = "2.1M",
            thumbnailUrl = "https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
            durationSeconds = 880,
            viewCount = 920000,
            uploadDateText = "3 weeks ago",
            likesCount = 67000,
            dislikesCount = 890,
            category = "Tech",
            ambientColor = Color(0xFF009688)
        )
    )

    val sampleShorts = listOf(
        ShortItem(
            id = "short_1",
            title = "Kotlin coroutines in 30 seconds! 🔥 #android #coding",
            creatorName = "DevQuickies",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop",
            likesCount = 145000,
            commentsCount = 2300,
            soundTrackTitle = "DevQuickies • Original Audio - Lofi Beat #4"
        ),
        ShortItem(
            id = "short_2",
            title = "Insane drone footage through the Alps mountain pass 🏔️",
            creatorName = "FPV Explorer",
            creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop",
            likesCount = 892000,
            commentsCount = 8400,
            soundTrackTitle = "Hans Zimmer • Interstellar Theme Remix"
        ),
        ShortItem(
            id = "short_3",
            title = "Satisfying 3D Animation with Blender Geometry Nodes ✨",
            creatorName = "MotionStudio",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop",
            likesCount = 420000,
            commentsCount = 3100,
            soundTrackTitle = "Synthwave Chill • Neon Nights"
        )
    )

    val sampleComments = listOf(
        CommentItem(
            id = "comm_1",
            videoId = "vid_1",
            authorName = "Android Dev Master",
            authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
            text = "📌 Timestamps for quick reference:\n00:00 Intro\n01:25 Room DB Schema\n03:10 HLS Player\n05:40 Gesture Controls\n07:50 YouTube Premium Features! Enjoy building!",
            timeAgo = "1 day ago",
            likesCount = 1840,
            isPinned = true,
            hasCreatorHeart = true,
            timestampSecond = 85
        ),
        CommentItem(
            id = "comm_2",
            videoId = "vid_1",
            authorName = "Rafiqul Islam",
            authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop",
            text = "03:10 The adaptive bitrate explanation was pure gold! Can you also explain DASH manifest parsing?",
            timeAgo = "18 hours ago",
            likesCount = 420,
            hasCreatorHeart = true,
            timestampSecond = 190,
            replies = listOf(
                CommentItem(
                    id = "comm_2_rep_1",
                    videoId = "vid_1",
                    authorName = "Android Dev Master",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
                    text = "Glad you enjoyed it! Yes, DASH manifest parsing will be covered in part 2 next Tuesday!",
                    timeAgo = "12 hours ago",
                    likesCount = 89,
                    hasCreatorHeart = true
                )
            )
        ),
        CommentItem(
            id = "comm_3",
            videoId = "vid_1",
            authorName = "Elena Rostova",
            authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop",
            text = "The Ambient Mode glow effect around the video player looks breathtaking on AMOLED displays! ⭐",
            timeAgo = "14 hours ago",
            likesCount = 275
        )
    )

    val sampleLiveMessages = listOf(
        LiveChatMessage("msg_1", "Tanvir Ahmed", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", "Greetings from Dhaka, Bangladesh! 🇧🇩"),
        LiveChatMessage("msg_2", "Sarah Connor", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", "Audio and video are crystal clear 1080p60!"),
        LiveChatMessage(
            id = "msg_3",
            authorName = "Michael Scott",
            authorAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
            message = "Keep up the incredible content! $10 Super Chat 🚀",
            isSuperChat = true,
            superChatAmount = "$10.00",
            superChatColor = Color(0xFF00B0FF)
        ),
        LiveChatMessage("msg_4", "Dev Moderator", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "Remember to adhere to community guidelines in chat.", isModerator = true),
        LiveChatMessage(
            id = "msg_5",
            authorName = "Apex Supporter",
            authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            message = "Big love to the team! $50 Super Chat! 🔥🎉",
            isSuperChat = true,
            superChatAmount = "$50.00",
            superChatColor = Color(0xFFFFD600)
        )
    )

    val sampleCommunityPosts = listOf(
        CommunityPost(
            id = "post_1",
            channelName = "Android Dev Master",
            channelAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
            timeAgo = "1 day ago",
            contentText = "Hey creators & developers! Which upcoming topic would you like us to explore in our next deep-dive session?",
            pollQuestion = "Next YouTube Architecture Deep Dive:",
            pollOptions = listOf(
                PollOption("opt_1", "Real-Time RTMP Live Streaming Ingestion", 580, true),
                PollOption("opt_2", "Offline Video DRM & Chunk Encryption", 340),
                PollOption("opt_3", "Adaptive DASH / HLS Bitrate Algorithms", 290)
            ),
            likesCount = 3800,
            commentsCount = 240
        ),
        CommunityPost(
            id = "post_2",
            channelName = "Android Dev Master",
            channelAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop",
            timeAgo = "3 days ago",
            contentText = "⭐ Channel Members Exclusive: We've just uploaded the complete source code and custom icons package for the YouTube clone project. Check the Community link below!",
            likesCount = 1900,
            commentsCount = 78,
            isMemberOnly = true
        )
    )
}
