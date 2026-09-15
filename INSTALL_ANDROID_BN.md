# MyTube Android ফোনে ইন্সটল গাইড 📱

আমিনের জন্য ৫টি উপায় - সবচেয়ে সহজ থেকে Play Store পর্যন্ত

---

## ⚡ Method 1: PWA Install (1-Click, 10 সেকেন্ড) - RECOMMENDED FOR NOW

**কোনো APK, Android Studio, কোড লাগবে না!**

### Android Chrome এ:

1. **এই ওয়েবসাইট Chrome এ ওপেন করুন** (যে লিংকে আছেন - Preview লিংক বা Vercel লিংক)
2. **নিচে লাল Banner আসবে** → "📲 ইন্সটল করুন" বাটন চাপুন
3. **অথবা Chrome Menu** (উপরে ডানে ⋮) → **"Add to Home screen"** বা **"Install App"** বা **"অ্যাপ ইন্সটল করুন"**
4. **Install** চাপুন
5. **হোম স্ক্রিনে MyTube আইকন** আসবে!
6. **আইকনে চাপুন** → YouTube এর মতো Fullscreen App খুলবে, Address Bar থাকবে না!

### iPhone Safari তে:

1. Safari তে ওপেন করুন
2. Share বাটন (⎙ নিচে মাঝখানে) → Add to Home Screen → Add

### PWA কেন ভালো?

- ⚡ **Super Fast** - Offline কাজ করে
- 📦 **1MB এরও কম** - ফোন মেমরি বাঁচে
- 🔄 **Auto Update** - নতুন ভার্সন auto update
- 🔔 **Push Notification** - ভবিষ্যতে যোগ করা যায়
- ✅ **No Play Store** - সরাসরি install

### PWA কিভাবে কাজ করে? (শেখার জন্য)

```javascript
// 1. manifest.json - App এর পরিচয়
{
  "name": "MyTube",
  "display": "standalone", // Fullscreen App
  "icons": [...],
  "start_url": "/"
}

// 2. sw.js - Service Worker (Offline support)
self.addEventListener('fetch', (event) => {
  // Network first, then cache
  event.respondWith(fetch(event.request).catch(() => caches.match(event.request)))
})

// 3. beforeinstallprompt - Install Banner
window.addEventListener('beforeinstallprompt', (e) => {
  e.preventDefault()
  deferredPrompt = e // Save for later
  showInstallButton() // "Install" বাটন দেখাও
})

// 4. Install
deferredPrompt.prompt() // Chrome এর Install Popup দেখাবে
```

---

## 📦 Method 2: WebView APK (Android Studio, 30 মিনিট)

**নিজের APK ফাইল বানানো, বন্ধুদের শেয়ার করা যায়**

### Step 1: Android Studio Install

- https://developer.android.com/studio → Download → Install

### Step 2: New Project

```
Android Studio → New Project → Empty Activity
Name: MyTube
Package: com.amin.mytube
Language: Kotlin
Minimum SDK: API 21 (Android 5.0)
```

### Step 3: Permission (AndroidManifest.xml)

```xml
<manifest>
  <uses-permission android:name="android.permission.INTERNET" />
  <application
    android:usesCleartextTraffic="true"
    android:hardwareAccelerated="true"
    ...>
  </application>
</manifest>
```

### Step 4: Layout (activity_main.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<WebView
  xmlns:android="http://schemas.android.com/apk/res/android"
  android:id="@+id/webView"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```

### Step 5: Code (MainActivity.kt)

`android-guide/MainActivity.kt` ফাইল থেকে কপি করুন:

```kotlin
package com.amin.mytube

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  private lateinit var webView: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    webView = findViewById(R.id.webView)
    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    webView.settings.allowFileAccess = true
    webView.settings.mediaPlaybackRequiresUserGesture = false
    webView.webViewClient = WebViewClient()
    
    // তোমার Vercel URL বা Local IP
    // Emulator: http://10.0.2.2:5173
    // Real Device: http://192.168.1.xxx:5173 (same WiFi)
    // Production: https://your-mytube.vercel.app
    webView.loadUrl("https://your-mytube.vercel.app")
  }

  override fun onBackPressed() {
    if (webView.canGoBack()) webView.goBack()
    else super.onBackPressed()
  }
}
```

### Step 6: Build APK

```
Android Studio → Build → Build Bundle(s) / APK(s) → Build APK(s)
→ Wait → app/build/outputs/apk/debug/app-debug.apk
→ ফোনে কপি → Install
```

**ফোনে Install করতে:**

- ফোনে Unknown Sources Enable: Settings → Security → Install Unknown Apps → Allow
- APK ফাইলে ক্লিক → Install

---

## 🔋 Method 3: Capacitor (Modern, Best for Production)

**এক কোডে Web + Android + iOS, Native Features যোগ করা যায়**

### Install

```bash
# MyTube project folder এ
npm install @capacitor/core @capacitor/cli @capacitor/android

# Init
npx cap init MyTube com.amin.mytube --web-dir=dist

# Build Web
npm run build

# Add Android
npx cap add android

# Sync (Web code → Android)
npx cap sync

# Open Android Studio
npx cap open android

# Run on Phone (USB Debugging ON, USB Connected)
# Android Studio → Run Button → Select Device
```

### Native Features Add করা

```javascript
// Push Notification
import { PushNotifications } from '@capacitor/push-notifications'

// Camera
import { Camera } from '@capacitor/camera'

// Example
const takePhoto = async () => {
  const photo = await Camera.getPhoto({
    quality: 90,
    resultType: CameraResultType.Uri
  })
}
```

### APK Build

```
Android Studio → Build → Generate Signed Bundle / APK → APK → Create Keystore → Build
```

---

## 🏪 Method 4: Play Store Publish (TWA - Trusted Web Activity)

**PWA কে Play Store App বানানো - Google Official Way**

### Option A: PWABuilder (Easy, No Code - 10 মিনিট)

1. https://www.pwabuilder.com/ এ যান
2. আপনার MyTube URL দিন (যেমন https://mytube.vercel.app)
3. Start → Analyze
4. Package For Store → Android → Generate Package
5. Download → .aab ফাইল পাবেন
6. https://play.google.com/console → Create App → Upload AAB

### Option B: Bubblewrap (Google Official CLI)

```bash
npm install -g @bubblewrap/cli

# Init (manifest.json থেকে)
bubblewrap init --manifest https://your-mytube.vercel.app/manifest.json

# Build
bubblewrap build
# → app-release-signed.apk + app-release-bundle.aab

# Install on phone
bubblewrap install
```

### Play Store Requirements

- Google Play Console Account: $25 one-time fee
- App Icon: 512x512 PNG
- Feature Graphic: 1024x500
- Screenshots: Phone, Tablet
- Privacy Policy URL
- Package Name: com.amin.mytube (unique, once set can't change)
- Signing Key: Keep safe!

---

## 🎥 Method 5: Quick Share (No Install, Just Link)

**বন্ধুদের সাথে শেয়ার করার সবচেয়ে সহজ উপায়**

1. Vercel এ Deploy করুন:
```bash
npm install -g vercel
vercel --prod
# → https://mytube-xyz.vercel.app লিংক পাবেন
```

2. লিংক শেয়ার করুন - যে কেউ Chrome এ ওপেন করে Install করতে পারবে!

---

## 🆘 Common Problems

### PWA Install Banner আসছে না?

- Chrome ব্যবহার করুন (Firefox এ PWA support কম)
- HTTPS লাগবে (localhost বা Vercel HTTPS ok)
- manifest.json + sw.js থাকতে হবে (আমরা add করেছি)
- 2-3 বার visit করতে হতে পারে

### WebView এ YouTube ভিডিও চলছে না?

```kotlin
webView.settings.mediaPlaybackRequiresUserGesture = false
webView.settings.javaScriptEnabled = true
webView.webChromeClient = WebChromeClient() // Important for video
```

### APK Install হচ্ছে না?

- Settings → Security → Unknown Sources → Allow
- আগের version uninstall করে আবার try করুন

### Capacitor Sync Error?

```bash
npm run build # dist folder তৈরি করতে হবে
npx cap sync
```

---

## 🚀 আমিনের জন্য Recommendation

**আজকের জন্য:**

1. **PWA Install** করুন (10 সেকেন্ড) - হোম স্ক্রিনে App পাবেন
2. বন্ধুদের Vercel লিংক শেয়ার করুন

**এই সপ্তাহে:**

1. Android Studio Install → WebView APK বানান
2. বন্ধুদের APK শেয়ার করুন

**এই মাসে:**

1. Capacitor শিখুন → Native Features যোগ করুন
2. Play Store Console Account খুলুন ($25)
3. PWABuilder দিয়ে AAB বানিয়ে Play Store এ Upload করুন!

---

## 📚 Resources

- PWA Docs: https://web.dev/progressive-web-apps/
- Capacitor Docs: https://capacitorjs.com/docs
- Bubblewrap: https://github.com/GoogleChromeLabs/bubblewrap
- PWABuilder: https://www.pwabuilder.com/
- Play Console: https://play.google.com/console

---

**Happy Installing! 🎉 আমিন, এখন তোমার MyTube ফোনে চলবে YouTube এর মতো!**
