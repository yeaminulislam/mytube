# Vanced / ReVanced কিভাবে কাজ করে? - Full Technical Deep Dive (Bangla)

আমিনের জন্য - শেখার মজার জন্য!

---

## 1. YouTube Vanced এর ইতিহাস

- **2017:** YouTube Vanced শুরু - XDA Developers
- **2022 March:** Google Legal Notice → Vanced বন্ধ
- **2022 July:** ReVanced শুরু - Community driven, Open Source
- **এখন:** ReVanced Manager + ReVanced Patches

---

## 2. Vanced এর 2 টা Version

### A. Non-Root (বেশিরভাগ মানুষ এটা ব্যবহার করে)

```
Phone (No Root)
├── YouTube ReVanced APK (Patched)
└── ReVanced GmsCore (MicroG) - Fake Google Play Service
    └── Google Login কাজ করে
```

### B. Root (Rooted Phone)

```
Phone (Rooted)
├── Original YouTube App Replace করে ReVanced
└── No need GmsCore - Real Google Play Service ব্যবহার করে
```

---

## 3. Real Vanced কিভাবে বানানো হয়? Step by Step

### Step 1: Official YouTube APK Download

```bash
# APKMirror থেকে Compatible Version Download
# যেমন: youtube_20.14.46.apk (ReVanced Patches এর জন্য specific version লাগে)
wget https://www.apkmirror.com/apk/google-inc/youtube/youtube-20-14-46-release/
```

### Step 2: Decompile (apktool)

```bash
# apktool - APK → Smali Code
apktool d youtube_20.14.46.apk -o youtube_decompiled

# Structure:
youtube_decompiled/
├── AndroidManifest.xml
├── smali/
│   └── com/google/android/apps/youtube/
│       ├── PlayerController.smali
│       ├── Ads/
│       └── ...
├── res/
└── ...
```

**Smali কি?** Java Bytecode এর Human Readable Form. Java → .class → .dex → smali

### Step 3: Patches Apply (ReVanced Patches)

ReVanced Patches - Kotlin/Java এ লেখা, Smali Code Modify করে

**Example: AdBlock Patch**

```kotlin
// ReVanced Patch: hide-ads.patch
@Patch
class HideAdsPatch {
  @PatchMethod
  fun hideAds() {
    // Find: showAds() method
    // Replace with: return-void (কিছু না করে return)
  }
}

// Smali Level:
// Original:
// .method public showAds()V
//   invoke-static {v0}, Lcom/google/android/libraries/ads;->show()V
//   return-void
// .end method

// Patched:
// .method public showAds()V
//   return-void  // Ad দেখানোর আগেই return!
// .end method
```

**Other Patches:**

```kotlin
// Background Playback Patch
// Original: if (appInBackground) pauseVideo()
// Patched: if (false) pauseVideo() → Never pause!

// SponsorBlock Patch
// Add: SponsorBlock API call + auto skip logic

// AMOLED Patch
// Find: #FFFFFF (white) → Replace with #000000 (black)

// Premium Heading Patch
// Remove: "Get YouTube Premium" banner
```

### Step 4: Recompile

```bash
apktool b youtube_decompiled -o youtube_patched.apk
```

### Step 5: Sign APK

```bash
# Keystore Create
keytool -genkey -v -keystore myvanced.keystore -alias myvanced -keyalg RSA -keysize 2048 -validity 10000

# Sign
apksigner sign --ks myvanced.keystore --ks-key-alias myvanced youtube_patched.apk
```

### Step 6: Install + MicroG

```bash
# Non-Root needs GmsCore for Login
adb install revanced-gmscore.apk
adb install youtube_patched.apk
```

**MicroG / GmsCore কি?**

Google Play Service Fake করে:

```java
// Real Google Play Service:
// com.google.android.gms.auth.GoogleAuthUtil.getToken()

// MicroG - Same package name, Fake implementation:
package com.google.android.gms.auth;

public class GoogleAuthUtil {
  public static String getToken(Context ctx, String account, String scope) {
    // Return fake token, but actually get real token via WebView OAuth
    return realGoogleToken;
  }
}
```

YouTube App মনে করে Real Google Play Service আছে, Login কাজ করে!

---

## 4. Vanced এর Main Features কিভাবে কাজ করে?

### A. AdBlock

**2 টা জায়গায় Ad আসে YouTube এ:**

1. **Video Ads (Pre-roll, Mid-roll):** `googlevideo.com` থেকে Ad segments
2. **UI Ads (Home, Search):** `youtube.com/api/ads`

**Vanced কিভাবে Block করে:**

```smali
# Method 1: Block at Player Level
# PlayerController.smali
.method public shouldShowAd()Z
  const/4 v0, 0x0  # false
  return v0
.end method

# Method 2: Block Ad URLs
# NetworkController.smali
.method public isAdUrl(Ljava/lang/String;)Z
  const-string v0, "googleadservices.com"
  invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/String;)Z
  move-result v0
  if-eqz v0, :not_ad
  const/4 v0, 0x1  # true = is ad
  return v0
  :not_ad
  const/4 v0, 0x0
  return v0
.end method
```

### B. Background Playback

**Original YouTube (Free):** App background এ গেলে `pauseVideo()`

**Vanced:** `pauseVideo()` call টা remove

```smali
# PlayerService.smali
.method public onAppBackgrounded()V
  # Original:
  # invoke-virtual {p0}, Lcom/google/android/apps/youtube/app/PlayerService;->pauseVideo()V
  
  # Patched: Do nothing
  return-void
.end method

# Plus Foreground Service for Notification Controls:
# .method public startForegroundService()V
#   invoke-virtual {p0}, Lcom/google/android/apps/youtube/app/PlayerService;->startForeground()V
```

**Android Foreground Service:**

```kotlin
// Vanced uses Foreground Service to keep playing
class BackgroundPlaybackService : Service() {
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    val player = ExoPlayer.Builder(this).build()
    player.playWhenReady = true
    
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Playing in background")
      .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
      .build()
    
    startForeground(1, notification) // System won't kill it
    return START_STICKY
  }
}
```

### C. SponsorBlock

**Community Driven - User রা Sponsor মার্ক করে**

```kotlin
// SponsorBlock API Integration
class SponsorBlockController {
  fun getSegments(videoId: String): List<Segment> {
    val url = "https://sponsor.ajay.app/api/skipSegments?videoID=$videoId"
    val response = httpClient.get(url)
    return response.json() // [{segment: [0, 10], category: "sponsor"}]
  }
  
  fun shouldSkip(currentTime: Double): Double? {
    for (segment in segments) {
      if (currentTime in segment.start..segment.end) {
        return segment.end // Skip to end
      }
    }
    return null
  }
}

// Player Listener
player.addListener(object : Player.Listener {
  override fun onPositionDiscontinuity(...) {
    val skipTo = sponsorBlock.shouldSkip(player.currentPosition)
    if (skipTo != null) player.seekTo(skipTo)
  }
})
```

### D. Return YouTube Dislike

```kotlin
// YouTube 2021 এ Dislike Count Hide করে দিয়েছিল
// RYD (Return YouTube Dislike) - Extension + API

// API: https://returnyoutubedislikeapi.com/votes?videoId=VIDEO_ID
// Response: {likes: 1000, dislikes: 50}

class DislikeController {
  fun fetchDislikes(videoId: String) {
    val url = "https://returnyoutubedislikeapi.com/votes?videoId=$videoId"
    val data = httpClient.get(url).json()
    showDislikeCount(data.dislikes)
  }
}
```

### E. AMOLED Black Theme

```smali
# Colors.xml patch
# Original: #FFFFFF (white), #212121 (dark gray)
# Patched: #000000 (true black)

# res/values/colors.xml
# <color name="yt_black">#FF000000</color> → #FF000000 (already black, but more places)
# <color name="yt_white">#FFFFFFFF</color> → #FF000000
```

---

## 5. MyVanced - Legal Educational Version (NewPipeExtractor)

**Vanced এর মতো, কিন্তু APK Patch না করে Scratch থেকে**

### NewPipeExtractor কি?

Open Source Library - YouTube Website Scraping করে Video URL বের করে, Official API ব্যবহার করে না

```kotlin
// NewPipeExtractor (Java)
val extractor = YouTubeStreamExtractorFactory().createExtractor("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
extractor.fetchPage()
val videoStreams = extractor.videoStreams // Direct video URLs (googlevideo.com)
val title = extractor.name
val views = extractor.viewCount
```

**কিভাবে কাজ করে?**

1. YouTube Website HTML Download: `https://www.youtube.com/watch?v=VIDEO_ID`
2. HTML Parse: `ytInitialPlayerResponse` JSON খুঁজে বের করে
3. JSON থেকে Direct Video URLs Extract: `https://rr1---sn-...googlevideo.com/videoplayback?...`
4. ExoPlayer দিয়ে Play

**No API Key, No Google Account Needed!**

### MyVanced Android Architecture

```
MainActivity (Jetpack Compose UI)
├── Video List (RecyclerView / LazyColumn)
├── Player (ExoPlayer)
│   ├── Foreground Service (Background Play)
│   ├── PiP (Picture-in-Picture)
│   └── SponsorBlock (Auto Skip)
├── Extractor (NewPipeExtractor)
│   ├── Search
│   ├── Video Details
│   └── Related Videos
└── Features
    ├── AdBlock (Block ad URLs in ExoPlayer)
    ├── AMOLED Theme
    ├── Download (Save video file)
    └── Return Dislike (RYD API)
```

---

## 6. শেখার জন্য কি করবে?

### Option A: Vanced Patching শিখতে (Hacker Way)

```bash
# Tools Install
sudo apt install apktool jadx dex2jar

# YouTube APK Download
# Decompile
apktool d youtube.apk

# Smali Code পড়ো
cat smali/com/google/android/apps/youtube/app/PlayerService.smali

# Jadx - Java Code দেখো (Smali থেকে Java)
jadx youtube.apk -d youtube_java

# Try to find Ad logic, Background logic
grep -r "showAds" youtube_java/
grep -r "pauseVideo" youtube_java/
```

### Option B: MyVanced (NewPipeExtractor) - Recommended for Learning

```bash
# Clone NewPipeExtractor
git clone https://github.com/TeamNewPipe/NewPipeExtractor.git

# Study Extractor
cat extractor/src/main/java/org/schabi/newpipe/extractor/services/youtube/YoutubeStreamExtractor.java

# Build MyVanced Android App (myvanced/android/)
# Use ExoPlayer + NewPipeExtractor
```

---

## 7. Legal & Ethics

- **Vanced:** Grey Area - YouTube ToS Violate, Google Legal Notice পাঠিয়েছিল, তাই Vanced বন্ধ
- **ReVanced:** Open Source Patches, User নিজে Patch করে - Legal Grey Area তে থাকে
- **NewPipe / MyVanced:** Legal - Open Source, No APK Patching, Scraping (YouTube Website Public Data)
- **MyTube (Official API):** 100% Legal - Google Official API

**Educational Purpose এর জন্য সবই শেখা যায়, কিন্তু Play Store এ Publish করতে হলে Official API বা NewPipeExtractor Way ব্যবহার করো!**

---

## 8. Resources

- ReVanced Patches: https://github.com/ReVanced/revanced-patches
- ReVanced Manager: https://github.com/ReVanced/revanced-manager
- NewPipeExtractor: https://github.com/TeamNewPipe/NewPipeExtractor
- NewPipe App: https://github.com/TeamNewPipe/NewPipe
- SponsorBlock API: https://wiki.sponsor.ajay.app/
- Return YouTube Dislike: https://returnyoutubedislike.com/

---

**Enjoy Hacking! 🚀 আমিন, এখন তুমি Vanced এর ভিতরের সব জানো!**
