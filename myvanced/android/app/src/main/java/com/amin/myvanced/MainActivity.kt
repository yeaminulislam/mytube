package com.amin.myvanced

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amin.myvanced.extractor.YouTubeExtractor
import com.amin.myvanced.service.PlaybackService

/**
 * MyVanced - Main Activity
 * Vanced এর মতো YouTube Client - Educational
 * 
 * Features:
 * - NewPipeExtractor for YouTube (No API Key, No Google Account)
 * - ExoPlayer with Background Play
 * - SponsorBlock Auto Skip
 * - AdBlock (Block ad URLs)
 * - AMOLED Theme
 */

class MainActivity : ComponentActivity() {
    
    private val extractor = YouTubeExtractor()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MyVancedTheme {
                MyVancedApp()
            }
        }
    }
}

@Composable
fun MyVancedTheme(content: @Composable () -> Unit) {
    // AMOLED Black Theme - Vanced Style
    val darkColorScheme = darkColorScheme(
        background = androidx.compose.ui.graphics.Color(0xFF000000), // True Black
        surface = androidx.compose.ui.graphics.Color(0xFF000000),
        primary = androidx.compose.ui.graphics.Color(0xFFFF0000) // YouTube Red
    )
    
    MaterialTheme(
        colorScheme = darkColorScheme,
        content = content
    )
}

@Composable
fun MyVancedApp() {
    var searchQuery by remember { mutableStateOf("android development") }
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header - Vanced Style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("MyVanced", style = MaterialTheme.typography.headlineMedium)
            Text("Vanced Clone", style = MaterialTheme.typography.labelSmall)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search YouTube (No API Key!)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = {
                isLoading = true
                // In real app: extractor.search(searchQuery) { result -> videos = result }
                // For demo: Mock
                videos = listOf(
                    VideoItem("dQw4w9WgXcQ", "Android Development Full Course", "Learn With Amin", "1.2M views"),
                    VideoItem("9bZkp7q19f0", "Vanced Technique Explained", "Amin Dev", "500K views")
                )
                isLoading = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔍 Search (NewPipeExtractor)")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Vanced Features Toggles
        VancedFeaturesPanel()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Video List
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(videos) { video ->
                    VideoCard(video = video)
                }
            }
        }
    }
}

@Composable
fun VancedFeaturesPanel() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("🔧 Vanced Features", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            FeatureToggle("🚫 AdBlock", "Block YouTube Ads", true)
            FeatureToggle("🎵 Background Play", "Play in background", true)
            FeatureToggle("💰 SponsorBlock", "Auto skip sponsors", true)
            FeatureToggle("👎 Return Dislike", "Show dislike count", true)
            FeatureToggle("⚫ AMOLED Black", "True black theme", true)
            FeatureToggle("📺 PiP", "Picture-in-Picture", true)
        }
    }
}

@Composable
fun FeatureToggle(name: String, desc: String, initial: Boolean) {
    var enabled by remember { mutableStateOf(initial) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = enabled, onCheckedChange = { enabled = it })
    }
}

@Composable
fun VideoCard(video: VideoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(video.title, style = MaterialTheme.typography.titleMedium)
            Text(video.channel, style = MaterialTheme.typography.bodySmall)
            Text(video.views, style = MaterialTheme.typography.bodySmall)
        }
    }
}

data class VideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val views: String
)
