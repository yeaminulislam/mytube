/**
 * MyTube Native - Jetpack Compose Example
 * এটি হলো Future Plan - যখন তুমি Native Android শিখবে
 * 
 * আমিনের জন্য: এই কোডটি বুঝতে সময় লাগবে, কিন্তু এটাই আসল YouTube Clone এর Structure
 */

package com.amin.mytube.native

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface

// 1. Data Model (YouTube API থেকে আসবে)
data class Video(
    val id: String,
    val title: String,
    val channel: String,
    val thumbnail: String,
    val views: String,
    val duration: String
)

// 2. Home Screen - YouTube এর মতো
@Composable
fun HomeScreen(videos: List<Video>, onVideoClick: (Video) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(videos) { video ->
            VideoCard(video = video, onClick = { onVideoClick(video) })
        }
    }
}

@Composable
fun VideoCard(video: Video, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Thumbnail with duration
            Box {
                // AsyncImage(model = video.thumbnail)
                Text(
                    text = video.duration,
                    modifier = Modifier.padding(4.dp)
                )
            }
            // Title, Channel, Views
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = video.title, style = MaterialTheme.typography.titleMedium)
                Text(text = video.channel, style = MaterialTheme.typography.bodySmall)
                Text(text = "${video.views} views", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// 3. Video Player Screen - ExoPlayer
@Composable
fun VideoPlayerScreen(videoId: String) {
    // ExoPlayer setup
    // val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    // PlayerSurface(player = exoPlayer)
    
    Column {
        // Player
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)) {
            Text("ExoPlayer here - Playing $videoId")
        }
        // Title, Like, Subscribe
        // Comments
    }
}

// 4. Shorts Screen - Vertical ViewPager2
@Composable
fun ShortsScreen(shorts: List<Video>) {
    // VerticalPager in Compose
    // val pagerState = rememberPagerState()
    // VerticalPager(state = pagerState) { page ->
    //     ShortsItem(video = shorts[page])
    // }
}

// 5. Retrofit - YouTube Data API
/*
interface YouTubeApi {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): YouTubeResponse
}

// ViewModel
class HomeViewModel : ViewModel() {
    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos
    
    fun search(query: String) {
        viewModelScope.launch {
            val result = youtubeApi.searchVideos(q = query, apiKey = API_KEY)
            _videos.value = result.toDomain()
        }
    }
}
*/

// Learning Tips for Amin:
// 1. প্রথমে XML + RecyclerView দিয়ে Home Screen বানাও
// 2. তারপর ExoPlayer যোগ করো
// 3. Firebase Auth দিয়ে Login বানাও
// 4. সবশেষে Jetpack Compose এ migrate করো
