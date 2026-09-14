# অ্যান্ড্রয়েড গাইড - আমিনের জন্য

## এই ওয়েব MyTube কে অ্যান্ড্রয়েড অ্যাপ বানানোর ৩ ধাপ

### ধাপ ১: WebView (আজকেই করতে পারবে - ১০ মিনিট)

1. Android Studio খুলো
2. New Project -> Empty Activity
3. `AndroidManifest.xml` এ Internet permission যোগ করো:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

4. `activity_main.xml`:
```xml
<WebView
    android:id="@+id/webView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

5. `MainActivity.kt` - উপরের ফাইলটি কপি করো

6. Run করো! তোমার MyTube এখন অ্যান্ড্রয়েড অ্যাপ! 🎉

### ধাপ ২: Capacitor (PWA to Native - ৩০ মিনিট)

এটি Best উপায় - এক কোডে Web + Android + iOS

```bash
# MyTube প্রজেক্টে
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init MyTube com.amin.mytube --web-dir=dist
npm run build
npx cap add android
npx cap sync
npx cap open android
```

Android Studio খুলবে, Run বাটন চাপো!

### ধাপ ৩: Native Jetpack Compose (৩ মাসের প্রজেক্ট)

এটি তোমার Portfolio Project হবে:

**Features to build:**
- Splash Screen
- Bottom Navigation (Home, Shorts, Subscriptions, You)
- Home: RecyclerView + Video Thumbnails (Glide)
- Video Player: ExoPlayer
- Shorts: ViewPager2 vertical
- Search: SearchView + Retrofit
- Login: Firebase Auth
- Like/Subscribe: Firebase Firestore

**YouTube Data API Integration:**
```kotlin
// build.gradle
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.github.bumptech.glide:glide:4.16.0")
implementation("androidx.media3:media3-exoplayer:1.2.0")
implementation("com.google.firebase:firebase-auth")
```

**Project Structure:**
```
com.amin.mytube/
├── ui/
│   ├── home/
│   ├── player/
│   ├── shorts/
│   └── auth/
├── data/
│   ├── api/
│   ├── model/
│   └── repository/
└── utils/
```

### Play Store এ পাবলিশ

1. Android Studio -> Build -> Generate Signed Bundle
2. Play Console এ অ্যাকাউন্ট খুলো ($25)
3. App Bundle আপলোড করো
4. Screenshots, Description দাও
5. Review এর জন্য Submit করো

### আমিনের জন্য টিপস

- প্রতিদিন ২ ঘন্টা কোড করো
- এই MyTube ওয়েব কোডটি ভালো করে বুঝো - Logic একই, শুধু Language আলাদা
- YouTube এ "Android Developer Bangladesh" চ্যানেল ফলো করো
- GitHub এ প্রজেক্ট পুশ করো - Portfolio হবে

শুভকামনা! 🚀
