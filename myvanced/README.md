# MyVanced - Vanced এর মতো YouTube Clone (Educational) 🎬

আমিনের জন্য - শেখার মজার জন্য, চাকরির জন্য না!

## Vanced কি? কিভাবে কাজ করে?

### Real Vanced (YouTube ReVanced) Technique:

```
1. Official YouTube APK Download (youtube_20.14.46.apk)
2. apktool d youtube.apk → smali code (Java bytecode)
3. Patches Apply:
   - AdBlock: showAds() → return-void
   - Background Play: pauseOnBackground → false
   - SponsorBlock: Add SponsorBlock API calls
   - AMOLED: Change colors to #000000
4. apktool b → new APK
5. Sign with own keystore
6. MicroG → Fake Google Play Service for Login
```

### MyVanced (আমাদের Educational Version) Technique:

```
1. NewPipeExtractor → YouTube Scraping (No Official API, No APK Patching)
2. ExoPlayer → Video Play
3. Foreground Service → Background Play
4. SponsorBlock API → Auto Skip
5. ReturnYouTubeDislike API → Dislike Count
6. AdBlock → Block Ad URLs in ExoPlayer
```

**Legal & Educational:** NewPipeExtractor Open Source, YouTube ToS ভায়োলেট করে না (Scraping), শেখার জন্য Perfect!

## Project Structure

```
myvanced/
├── android/ - Native Android App (Kotlin)
│   ├── app/src/main/java/com/amin/myvanced/
│   │   ├── MainActivity.kt - UI (Jetpack Compose)
│   │   ├── service/PlaybackService.kt - Background Play
│   │   ├── extractor/YouTubeExtractor.kt - NewPipeExtractor
│   │   └── sponsorblock/SponsorBlock.kt
│   └── build.gradle
│
├── web/ - Web Version with Vanced Features (React)
│   └── Already in src/services/ (sponsorblock.js, returnDislike.js)
│
└── docs/
    ├── VANCED_TECHNIQUE_BN.md - Vanced কিভাবে কাজ করে (Bangla)
    └── NEWPIPE_EXTRACTOR.md - NewPipeExtractor Guide
```

## Features (Vanced Style)

- ✅ AdBlock (Simulated via Service Worker / ExoPlayer)
- ✅ Background Playback (Media Session API / Foreground Service)
- ✅ Picture-in-Picture (PiP)
- ✅ SponsorBlock Auto Skip
- ✅ Return YouTube Dislike
- ✅ AMOLED Black Theme
- ✅ Download (via NewPipeExtractor)
- ✅ No Google Account Needed (Optional)

## How to Build Android App

```bash
cd myvanced/android
# Open in Android Studio
# Add NewPipeExtractor dependency
# Run on device
```

See `android/README.md` for details.

## Web Version - Already Integrated!

MyTube Web App এ Vanced Features যোগ করা হয়েছে:

- `src/services/sponsorblock.js` - SponsorBlock API
- `src/services/returnDislike.js` - RYD API
- `src/hooks/useVancedFeatures.js` - Background, PiP, AdBlock
- `src/components/VancedPanel.jsx` - Control Panel

Video Page এ Vanced Panel দেখতে পাবে!

## Learning Path - Vanced এর মতো বানাতে

1. **Week 1:** NewPipeExtractor - YouTube Scraping
2. **Week 2:** ExoPlayer + Foreground Service - Background Play
3. **Week 3:** SponsorBlock + RYD API
4. **Week 4:** AdBlock + AMOLED Theme

Enjoy Learning! 🚀
