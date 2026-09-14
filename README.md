# MyTube - Full Functional YouTube Clone 🎬 (Real YouTube API)

আমিনের জন্য - **Demo নয়, Real YouTube এর মতো Fully Functional!**

## 🚀 এখন কি কি Real?

### ✅ আগে ছিল Demo, এখন Real API Ready

| Feature | আগে (Mock) | এখন (Real API) |
|---------|------------|----------------|
| ভিডিও সার্চ | Mock filter | **YouTube Data API v3 /search** |
| Trending | Mock data | **/videos?chart=mostPopular** |
| Video Details | Mock | **/videos?id=...&part=statistics,contentDetails** |
| Comments | Mock | **/commentThreads?videoId=...** |
| Channel Info | Mock | **/channels?id=...** |
| Shorts | Mock | **/search?type=video&videoDuration=short** |
| Search Suggestions | Mock | **suggestqueries.google.com (YouTube Autocomplete)** |
| Google Login | Mock user | **Google Identity Services (GSI) + JWT + OAuth 2.0** |

---

## 🔑 কিভাবে Real API কানেক্ট করবেন? (3 মিনিট)

### Step 1: YouTube Data API Key

1. https://console.cloud.google.com/ -> New Project -> MyTube
2. APIs & Services -> Library -> **YouTube Data API v3** -> Enable
3. Credentials -> Create Credentials -> **API Key** -> Copy

### Step 2: Google OAuth Client ID

1. Credentials -> Create Credentials -> **OAuth Client ID**
2. Web Application -> 
   - Authorized JS origins: `http://localhost:5173`
   - Redirect URIs: `http://localhost:5173`
3. Client ID Copy

### Step 3: .env ফাইল

```bash
cp .env.example .env
# .env এ বসান:
VITE_YOUTUBE_API_KEY=AIzaSyD-YOUR-KEY
VITE_GOOGLE_CLIENT_ID=1234567890-abc.apps.googleusercontent.com
```

### Step 4: Run

```bash
npm install
npm run dev
# Console এ দেখবে: 🔑 YouTube API Mode: REAL
```

**Full Guide:** `GOOGLE_API_SETUP_BN.md` ফাইল দেখো - বাংলায় বিস্তারিত!

---

## 🧠 কিভাবে Google Client এর সাথে কথা বলে? (Code Walkthrough)

### 1. `src/services/youtubeApi.js` - YouTube এর সাথে কথা

```javascript
// Search - Real YouTube Search
async function searchVideos(query) {
  // Step 1: Search endpoint
  const searchUrl = `https://www.googleapis.com/youtube/v3/search?
    part=snippet
    &q=${query}
    &type=video
    &key=${API_KEY}` // API Key দিয়ে Auth
  
  const searchRes = await fetch(searchUrl)
  const searchData = await searchRes.json()
  // searchData.items = [{id: {videoId: "xyz"}, snippet: {title: "..."}}]

  // Step 2: Details (views, duration, likes) - আলাদা call কারণ search এ views থাকে না
  const videoIds = searchData.items.map(i => i.id.videoId).join(',')
  const detailsUrl = `https://www.googleapis.com/youtube/v3/videos?
    part=contentDetails,statistics,snippet
    &id=${videoIds}
    &key=${API_KEY}`

  const detailsRes = await fetch(detailsUrl)
  // detailsData.items[0].statistics.viewCount = "1234567"
  // detailsData.items[0].contentDetails.duration = "PT5M30S" (ISO 8601)

  return formattedVideos
}
```

**Quota Cost:**
- Search: 100 units
- Videos: 1 unit
- Comments: 1 unit
- Daily free: 10,000 units

### 2. `src/services/googleAuth.js` - Google Login এর সাথে কথা

#### A. GSI Load
```javascript
// Google Identity Services script load
<script src="https://accounts.google.com/gsi/client"></script>

await loadGoogleScript()
google.accounts.id.initialize({
  client_id: GOOGLE_CLIENT_ID,
  callback: handleCredentialResponse // Login হলে call হবে
})
```

#### B. ID Token (JWT) - User Info
```javascript
function handleCredentialResponse(response) {
  // response.credential = JWT (eyJhbGciOiJSUzI1NiIs...)
  // JWT = Header.Payload.Signature (Base64)
  
  const payload = decodeJwt(response.credential)
  // payload = {
  //   sub: "123", // Google User ID
  //   email: "amin@gmail.com",
  //   name: "Amin Islam",
  //   picture: "https://..."
  // }

  // Save user
  localStorage.setItem('mytube_user', JSON.stringify(user))
}
```

#### C. Access Token - YouTube Private Data
```javascript
// Like, Subscribe, History এর জন্য Access Token লাগে
const client = google.accounts.oauth2.initTokenClient({
  client_id: GOOGLE_CLIENT_ID,
  scope: "youtube.readonly youtube.force-ssl userinfo.email",
  callback: (res) => {
    // res.access_token = ya29.a0AfH6SMB...
    // এই token দিয়ে:
    fetch('https://www.googleapis.com/youtube/v3/subscriptions?mine=true', {
      headers: { Authorization: `Bearer ${res.access_token}` }
    })
  }
})
client.requestAccessToken() // Popup
```

---

## 📂 নতুন Project Structure

```
src/
├── services/
│   ├── youtubeApi.js      # 🔥 Real YouTube Data API v3
│   │   ├── searchVideos()         # /search
│   │   ├── getPopularVideos()     # /videos?chart=mostPopular
│   │   ├── getVideoDetails()      # /videos?id=
│   │   ├── getRelatedVideos()     # Related via search
│   │   ├── getVideoComments()     # /commentThreads
│   │   ├── getChannelDetails()    # /channels
│   │   ├── getSearchSuggestions() # suggestqueries.google.com
│   │   └── getShorts()            # /search?videoDuration=short
│   │
│   └── googleAuth.js      # 🔐 Real Google OAuth 2.0
│       ├── loadGoogleScript()     # GSI load
│       ├── decodeJwt()            # JWT decode
│       ├── initGoogleAuth()       # GSI init
│       ├── signInWithGoogle()     # Popup login
│       ├── getGoogleAccessToken() # OAuth2 token
│       └── signOutGoogle()
│
├── components/
│   ├── Header.jsx         # Search + Suggestions + User Menu
│   ├── Sidebar.jsx        # + API Debugger link
│   ├── VideoCard.jsx
│   ├── ShortsCard.jsx
│   └── ApiSetupBanner.jsx # API Key setup guide banner
│
├── pages/
│   ├── Home.jsx           # Real popular + search + shorts
│   ├── VideoPage.jsx      # Real details + comments + related + channel
│   ├── ShortsPage.jsx     # Real shorts
│   ├── Login.jsx          # Real Google button + JWT debug
│   └── ApiDebugger.jsx    # 🔧 Live API tester - Raw response দেখো
│
├── context/
│   └── AuthContext.jsx    # Real + Mock dual mode
│
└── data/
    └── mockVideos.js      # Fallback when no API key

server/
└── index.js               # Express proxy - API Key hide করার জন্য

.env.example               # API Keys template
GOOGLE_API_SETUP_BN.md     # Full setup guide in Bangla
```

---

## 🔧 API Debugger (Learning Lab)

`/debug` পেজে যাও:

- Live API call test করতে পারবে
- Raw JSON response দেখতে পারবে
- Code snippet দেখতে পারবে
- Mock vs Real mode status

```
http://localhost:5173/debug
```

---

## 🖥️ Backend Proxy (Optional but Pro)

Frontend এ API Key expose করা নিরাপদ নয়। তাই Backend:

```javascript
// server/index.js
app.get('/api/search', async (req, res) => {
  const { q } = req.query
  // API Key backend .env থেকে, frontend এ যায় না
  const url = `https://www.googleapis.com/youtube/v3/search?key=${process.env.YOUTUBE_API_KEY}&q=${q}`
  const data = await fetch(url).then(r => r.json())
  res.json(data)
})
```

Run:
```bash
npm run server
# Frontend .env: VITE_API_BASE_URL=http://localhost:3001
```

---

## 📱 Android এ Same Logic

### Retrofit (Android)
```kotlin
interface YouTubeApi {
  @GET("search")
  suspend fun search(
    @Query("q") q: String,
    @Query("key") key: String = BuildConfig.YOUTUBE_API_KEY
  ): SearchResponse
}
```

### Google Sign-In (Android)
```kotlin
val gso = GoogleSignInOptions.Builder()
  .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
  .requestEmail()
  .build()

val credential = GoogleAuthProvider.getCredential(idToken, null)
auth.signInWithCredential(credential)
```

**Details:** `android-guide/` ফোল্ডার দেখো

---

## 🚀 Run

```bash
# 1. Setup env
cp .env.example .env
# Edit .env with real keys

# 2. Install
npm install

# 3. Dev (frontend only)
npm run dev

# 4. Full stack (frontend + backend proxy)
npm run server # Terminal 1
npm run dev    # Terminal 2

# 5. Build
npm run build
```

---

## 🎓 আমিনের জন্য Learning Path

**Week 1: API Basics**
- [x] youtubeApi.js এর প্রতিটা function পড়ো
- [x] Browser Network tab এ API call দেখো
- [x] /debug পেজে test করো

**Week 2: Auth**
- [x] googleAuth.js - JWT কিভাবে decode হয়
- [x] OAuth 2.0 flow - ID Token vs Access Token
- [x] Google Cloud Console এ Quota Metrics দেখো

**Week 3: Android**
- [ ] Retrofit দিয়ে same API call Android এ
- [ ] ExoPlayer দিয়ে video play
- [ ] Firebase Auth + Google Sign-In

**Week 4: Publish**
- [ ] Vercel এ deploy
- [ ] Play Store এ WebView APK

---

## 📚 Docs

- YouTube Data API: https://developers.google.com/youtube/v3/docs
- Google Identity: https://developers.google.com/identity/gsi/web
- OAuth Playground: https://developers.google.com/oauthplayground/

---

**Made with ❤️ for Amin - Now Fully Functional! 🚀**
