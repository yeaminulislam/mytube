# MyTube - ইউটিউব ক্লোন 🎬 (Android Developer Learning Project)

আমিনের জন্য বিশেষভাবে তৈরি - একজন অ্যান্ড্রয়েড ডেভেলপার হওয়ার যাত্রায় প্রথম প্রজেক্ট!

## 🚀 লাইভ ডেমো
প্রজেক্টটি এখন চলছে: `npm run dev` -> http://localhost:5173

## ✨ ফিচারসমূহ

### 1. 🎥 ভিডিও দেখা
- YouTube এর মতো ভিডিও প্লেয়ার (YouTube Embed API)
- ভিডিওর টাইটেল, চ্যানেল, ভিউ, লাইক, ডিসলাইক
- রিলেটেড ভিডিও সাজেশন
- কমেন্ট সিস্টেম

### 2. 🔍 সার্চ
- রিয়েল-টাইম সার্চ (টাইটেল, চ্যানেল, ক্যাটাগরি অনুযায়ী)
- URL query support (`/?q=android`)
- ক্যাটাগরি ফিল্টার (সব, প্রোগ্রামিং, অ্যান্ড্রয়েড, ইত্যাদি)

### 3. 🔐 লগইন সিস্টেম
- ইমেইল/পাসওয়ার্ড লগইন (Demo - localStorage)
- Google দিয়ে লগইন (Mock)
- ইউজার প্রোফাইল মেনু
- লগআউট
- কমেন্ট করতে লগইন প্রয়োজন

### 4. 📱 YouTube Shorts
- ভার্টিকাল স্ক্রল (Snap scroll)
- কিবোর্ড ন্যাভিগেশন (Arrow Up/Down)
- লাইক, ডিসলাইক, শেয়ার, কমেন্ট
- সাবস্ক্রাইব
- ডেস্কটপে অ্যারো বাটন, মোবাইলে সোয়াইপ

### 5. 📱 Responsive & PWA Ready
- মোবাইল বটম ন্যাভিগেশন
- ডেস্কটপ সাইডবার (collapsible)
- PWA manifest (Android এ Install করা যাবে)
- YouTube এর মতো Dark Theme

## 🛠️ টেক স্ট্যাক

- **React 18** + **Vite**
- **React Router** - Navigation
- **Tailwind CSS** - Styling
- **Context API** - Auth State
- **YouTube Embed** - Video Player
- **LocalStorage** - Mock Auth

## 📂 প্রজেক্ট স্ট্রাকচার

```
src/
├── components/
│   ├── Header.jsx       # সার্চ, লোগো, ইউজার মেনু
│   ├── Sidebar.jsx      # YouTube স্টাইল সাইডবার
│   ├── VideoCard.jsx    # ভিডিও কার্ড
│   └── ShortsCard.jsx   # Shorts কার্ড
├── pages/
│   ├── Home.jsx         # হোম ফিড + Shorts + Search
│   ├── VideoPage.jsx    # ভিডিও প্লেয়ার পেজ
│   ├── ShortsPage.jsx   # Shorts ভার্টিকাল ফিড
│   └── Login.jsx        # লগইন/সাইনআপ
├── context/
│   └── AuthContext.jsx  # Auth Logic
└── data/
    └── mockVideos.js    # Mock Data
```

## 🤖 অ্যান্ড্রয়েড ডেভেলপার হওয়ার জন্য Next Steps

### এই ওয়েব অ্যাপকে অ্যান্ড্রয়েড অ্যাপে কনভার্ট করার ৩টি উপায়:

#### 1. WebView (সবচেয়ে সহজ - Beginner)
```kotlin
// Android Studio -> New Project -> Empty Activity
// activity_main.xml এ WebView যোগ করুন

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://your-mytube.vercel.app")
    }
}
```

#### 2. Capacitor (Recommended)
```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init MyTube com.amin.mytube
npx cap add android
npm run build
npx cap sync
npx cap open android
```

#### 3. Jetpack Compose + ExoPlayer (Pro - Native YouTube Clone)
এটি হবে তোমার আসল অ্যান্ড্রয়েড প্রজেক্ট:
- **UI**: Jetpack Compose
- **Video**: ExoPlayer / Media3
- **Shorts**: Vertical ViewPager2
- **Auth**: Firebase Auth
- **Backend**: Firebase Firestore + Storage
- **API**: Retrofit + YouTube Data API v3

### 📚 শেখার রোডম্যাপ (আমিনের জন্য)

**Month 1: Basics**
- [x] এই MyTube ওয়েব ক্লোন বুঝো (React)
- [ ] Java/Kotlin Basics
- [ ] Android Studio Setup
- [ ] Activity, Fragment, Intent

**Month 2: UI**
- [ ] XML Layouts / Jetpack Compose
- [ ] RecyclerView (YouTube এর Video List এর মতো)
- [ ] ViewPager2 (Shorts এর জন্য)

**Month 3: Advanced**
- [ ] Retrofit - YouTube API কল করা
- [ ] ExoPlayer - ভিডিও প্লে করা
- [ ] Firebase Auth - লগইন
- [ ] Room Database - Offline videos

**Month 4: MyTube Native**
- [ ] এই ডিজাইনকে Native Android এ বানাও
- [ ] Play Store এ পাবলিশ করো!

## 🚀 কিভাবে চালাবে

```bash
# 1. Install
npm install

# 2. Run
npm run dev

# 3. Build
npm run build
```

## 🔑 YouTube Data API (Real Data এর জন্য)

যদি Real YouTube ভিডিও দেখাতে চাও:

1. https://console.cloud.google.com এ যাও
2. YouTube Data API v3 Enable করো
3. API Key নাও
4. `.env` ফাইল তৈরি করো:
```
VITE_YOUTUBE_API_KEY=YOUR_API_KEY
```

তারপর `src/data/mockVideos.js` এর বদলে API কল করো:
```js
fetch(`https://www.googleapis.com/youtube/v3/search?part=snippet&q=android+development&key=${API_KEY}`)
```

## 🎨 UI Inspiration
- YouTube.com (Dark Mode)
- YouTube Mobile App
- Material Design 3

## 👨‍💻 Author
**Amin** - Aspiring Android Developer from Bangladesh 🇧🇩

> "একদিন আমি Play Store এ MyTube পাবলিশ করব!"

## 📝 License
MIT - Free for learning!

---
**Made with ❤️ for Amin's Android Developer Journey**
