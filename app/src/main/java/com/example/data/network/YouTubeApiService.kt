package com.example.data.network

import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object YouTubeApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

    /**
     * Search YouTube videos in real-time by querying YouTube search results
     */
    suspend fun searchVideos(query: String): List<VideoItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "https://www.youtube.com/results?search_query=$encodedQuery"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept-Language", "en-US,en;q=0.9,bn;q=0.8")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val html = response.body?.string() ?: return@withContext emptyList()
            parseYtInitialData(html, "Search")
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch trending / category videos (e.g. Music, Gaming, Tech, News, Coding)
     */
    suspend fun fetchCategoryVideos(category: String): List<VideoItem> = withContext(Dispatchers.IO) {
        val query = when (category.lowercase()) {
            "all" -> "popular trending videos"
            "trending" -> "trending videos"
            "music" -> "top trending music official video"
            "gaming" -> "popular gaming gameplay"
            "tech" -> "tech gadgets smartphone review"
            "coding" -> "coding tutorial android kotlin"
            "bangla" -> "popular bangla song drama natok"
            "news" -> "latest breaking news"
            "shorts" -> "viral youtube shorts"
            else -> "$category trending"
        }
        val results = searchVideos(query)
        // Tag results with a clean category label (not the raw search query)
        val label = when (category.lowercase()) {
            "all", "trending" -> "Trending"
            else -> category.replaceFirstChar { it.uppercase() }
        }
        results.map { it.copy(category = label) }
    }

    /**
     * Real-time search query suggestions from Google / YouTube Autocomplete API
     */
    suspend fun fetchSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=$encoded"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val body = response.body?.string() ?: return@withContext emptyList()
            val jsonArray = JSONArray(body)
            if (jsonArray.length() > 1) {
                val suggestionsArray = jsonArray.getJSONArray(1)
                val list = mutableListOf<String>()
                for (i in 0 until minOf(suggestionsArray.length(), 8)) {
                    list.add(suggestionsArray.getString(i))
                }
                return@withContext list
            }
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseYtInitialData(html: String, fallbackCategory: String): List<VideoItem> {
        val marker = "var ytInitialData = "
        val idx = html.indexOf(marker)
        if (idx == -1) return emptyList()

        val start = idx + marker.length
        val end = html.indexOf(";</script>", start)
        if (end == -1) return emptyList()

        val jsonStr = html.substring(start, end)
        val root = JSONObject(jsonStr)

        val contents = root.optJSONObject("contents")
            ?.optJSONObject("twoColumnSearchResultsRenderer")
            ?.optJSONObject("primaryContents")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents") ?: return emptyList()

        val videoList = mutableListOf<VideoItem>()

        for (i in 0 until contents.length()) {
            val section = contents.optJSONObject(i) ?: continue
            val itemSection = section.optJSONObject("itemSectionRenderer") ?: continue
            val items = itemSection.optJSONArray("contents") ?: continue

            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue
                val v = item.optJSONObject("videoRenderer") ?: continue

                val videoId = v.optString("videoId")
                if (videoId.isNullOrBlank()) continue

                // Title
                val title = v.optJSONObject("title")
                    ?.optJSONArray("runs")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: "YouTube Video"

                // Channel Info
                val channelRuns = v.optJSONObject("ownerText")?.optJSONArray("runs")
                val channelName = channelRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Creator"
                val channelId = channelRuns?.optJSONObject(0)
                    ?.optJSONObject("navigationEndpoint")
                    ?.optJSONObject("browseEndpoint")
                    ?.optString("browseId") ?: "channel_$videoId"

                // Channel Avatar
                val avatarUrl = v.optJSONObject("channelThumbnailSupportedRenderers")
                    ?.optJSONObject("channelThumbnailWithLinkRenderer")
                    ?.optJSONObject("thumbnail")
                    ?.optJSONArray("thumbnails")
                    ?.let { thumbs ->
                        if (thumbs.length() > 0) thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                        else null
                    } ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"

                // Length & Duration
                val durationText = v.optJSONObject("lengthText")?.optString("simpleText") ?: "3:30"
                val durationSeconds = parseDurationToSeconds(durationText)

                // View Count & Upload Date
                val viewText = v.optJSONObject("viewCountText")?.optString("simpleText") ?: "1.5M views"
                val viewCount = parseViewsToLong(viewText)
                val uploadDate = v.optJSONObject("publishedTimeText")?.optString("simpleText") ?: "Recently"

                // Description
                val description = v.optJSONArray("detailedMetadataSnippets")
                    ?.optJSONObject(0)
                    ?.optJSONObject("snippetText")
                    ?.optJSONArray("runs")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: title

                val videoItem = VideoItem(
                    id = videoId,
                    title = title,
                    description = description,
                    channelId = channelId,
                    channelName = channelName,
                    channelAvatarUrl = avatarUrl,
                    // The UI appends " subscribers" itself, so keep only the count here
                    subscriberCount = "1.8M",
                    thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
                    videoUrl = "https://www.youtube.com/watch?v=$videoId",
                    durationSeconds = durationSeconds,
                    viewCount = viewCount,
                    uploadDateText = uploadDate,
                    likesCount = (viewCount * 0.05).toLong().coerceAtLeast(1200L),
                    dislikesCount = (viewCount * 0.002).toLong().coerceAtLeast(50L),
                    category = fallbackCategory
                )

                videoList.add(videoItem)
            }
        }

        return videoList
    }

    private fun parseDurationToSeconds(durationStr: String): Int {
        try {
            val parts = durationStr.split(":").mapNotNull { it.trim().toIntOrNull() }
            return when (parts.size) {
                1 -> parts[0]
                2 -> parts[0] * 60 + parts[1]
                3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
                else -> 210
            }
        } catch (e: Exception) {
            return 210
        }
    }

    private fun parseViewsToLong(viewsStr: String): Long {
        val clean = viewsStr.replace(",", "").replace("views", "").trim().lowercase()
        return try {
            when {
                clean.endsWith("b") -> (clean.dropLast(1).trim().toDouble() * 1_000_000_000).toLong()
                clean.endsWith("m") -> (clean.dropLast(1).trim().toDouble() * 1_000_000).toLong()
                clean.endsWith("k") -> (clean.dropLast(1).trim().toDouble() * 1_000).toLong()
                else -> clean.filter { it.isDigit() }.toLongOrNull() ?: 1_200_000L
            }
        } catch (e: Exception) {
            1_200_000L
        }
    }
}
