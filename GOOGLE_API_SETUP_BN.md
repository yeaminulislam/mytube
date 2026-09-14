# MyTube - Real YouTube API Setup (Full Functional Guide) 🎬

আমিনের জন্য সম্পূর্ণ গাইড - কিভাবে Real YouTube এর মতো কাজ করবে, Google Client এর সাথে কিভাবে কথা বলে।

---

## 📌 কেন Real API দরকার?

Demo App এ আমরা Mock Data ব্যবহার করেছি। কিন্তু Real YouTube Clone বানাতে হলে:

1. **YouTube Data API v3** - Real ভিডিও, সার্চ, কমেন্ট, চ্যানেল
2. **Google OAuth 2.0** - Real Google Login, User এর Like/Subscribe
3. **Backend Proxy** (Optional) - API Key hide করা

---

## 🔑 Step 1: Google Cloud Project তৈরি

### 1.1 Console এ যান
- https://console.cloud.google.com/ এ যান
- Google Account দিয়ে Login করুন

### 1.2 New Project
```
Top Left -> Project Dropdown -> New Project
Project Name: MyTube
Location: No Organization
Create
```

### 1.3 Billing Enable (Free Tier)
- YouTube API Free, কিন্তু Billing enable করতে হয়
- Billing -> My Projects -> Enable Billing
- Free Trial $300 পাবেন

---

## 📺 Step 2: YouTube Data API v3 Enable

```
1. Left Menu -> APIs & Services -> Library
2. Search: "YouTube Data API v3"
3. Click -> Enable
4. Wait 1-2 minutes
```

**Quota:**
- দিনে 10,000 units free
- Search: 100 units per call
- Video Details: 1 unit per call
- Comments: 1 unit per call

মানে দিনে 100 বার Search + 9900 বার Video Details ফ্রি!

---

## 🔐 Step 3: API Key তৈরি (Public Data এর জন্য)

API Key দিয়ে Public Data পাওয়া যায় - যেমন ভিডিও সার্চ, Trending, Video Details।

```
1. APIs & Services -> Credentials
2. Create Credentials -> API Key
3. Key কপি করুন (AIzaSy...)
4. Edit API Key -> Restrict:
   - Application restrictions: None (Dev এর জন্য) / HTTP referrers (Prod)
   - API restrictions: Restrict key -> YouTube Data API v3
   - Save
```

**.env এ বসান:**
```env
VITE_YOUTUBE_API_KEY=AIzaSyD-EXAMPLE-1234567890
```

### কিভাবে কোডে কাজ করে? (`youtubeApi.js`)

```javascript
// 1. Search API Call
const searchUrl = `https://www.googleapis.com/youtube/v3/search?
  part=snippet
  &q=android development
  &type=video
  &key=${API_KEY}`

const searchResponse = await fetch(searchUrl)
const searchData = await searchResponse.json()
// searchData.items = [{id: {videoId: "abc"}, snippet: {title: "..."}}]

// 2. Video Details (views, duration, likes)
const videoIds = searchData.items.map(i => i.id.videoId).join(',')
const detailsUrl = `https://www.googleapis.com/youtube/v3/videos?
  part=contentDetails,statistics,snippet
  &id=${videoIds}
  &key=${API_KEY}`

const detailsResponse = await fetch(detailsUrl)
// detailsResponse.items = [{statistics: {viewCount: "123456"}, contentDetails: {duration: "PT5M30S"}}]

// 3. Format Data for UI
const videos = detailsData.items.map(item => ({
  id: item.id,
  title: item.snippet.title,
  channel: item.snippet.channelTitle,
  views: formatViewCount(item.statistics.viewCount),
  duration: parseDuration(item.contentDetails.duration)
}))
```

---

## 👤 Step 4: OAuth 2.0 Client ID (Google Login এর জন্য)

OAuth দিয়ে User এর Private Data - যেমন তার Subscriptions, Liked Videos, History।

```
1. Credentials -> Create Credentials -> OAuth Client ID
2. Configure Consent Screen (প্রথমবার):
   - User Type: External
   - App Name: MyTube
   - User Support Email: আপনার ইমেইল
   - Developer Contact: আপনার ইমেইল
   - Save and Continue -> Scopes -> Save -> Test Users -> Add your email -> Save
3. OAuth Client ID:
   - Application Type: Web Application
   - Name: MyTube Web Client
   - Authorized JavaScript origins:
     http://localhost:5173
     http://localhost:3000
     https://yourdomain.com (Production)
   - Authorized Redirect URIs:
     http://localhost:5173
     http://localhost:5173/login
   - Create
4. Client ID কপি করুন (1234567890-abc.apps.googleusercontent.com)
```

**.env এ বসান:**
```env
VITE_GOOGLE_CLIENT_ID=1234567890-abcdefgh.apps.googleusercontent.com
```

### কিভাবে কোডে কাজ করে? (`googleAuth.js`)

#### Part A: Google Identity Services Load

```javascript
// index.html এ script load হয়
<script src="https://accounts.google.com/gsi/client"></script>

// googleAuth.js
await loadGoogleScript() // GSI load

// Initialize
google.accounts.id.initialize({
  client_id: GOOGLE_CLIENT_ID,
  callback: handleCredentialResponse, // Login হলে এই function call হবে
  auto_select: false,
  ux_mode: 'popup'
})
```

#### Part B: ID Token (JWT) - User Info

```javascript
function handleCredentialResponse(response) {
  // response.credential = JWT Token (eyJhbGciOiJSUzI1NiIs...)
  
  // JWT Decode (Header.Payload.Signature)
  const payload = decodeJwt(response.credential)
  // payload = {
  //   sub: "123456789", // User ID
  //   email: "amin@gmail.com",
  //   name: "Amin Islam",
  //   picture: "https://...",
  //   email_verified: true
  // }

  const user = {
    id: payload.sub,
    email: payload.email,
    name: payload.name,
    avatar: payload.picture,
    idToken: response.credential // Backend এ verify করতে পাঠানো যায়
  }

  localStorage.setItem('mytube_user', JSON.stringify(user))
}
```

#### Part C: Access Token (YouTube Private Data)

```javascript
// YouTube এর Like, Subscribe করতে Access Token লাগে
const client = google.accounts.oauth2.initTokenClient({
  client_id: GOOGLE_CLIENT_ID,
  scope: `
    https://www.googleapis.com/auth/youtube.readonly
    https://www.googleapis.com/auth/youtube.force-ssl
    https://www.googleapis.com/auth/userinfo.profile
  `,
  callback: (response) => {
    // response.access_token = ya29.a0AfH6SMB...
    localStorage.setItem('mytube_access_token', response.access_token)
    
    // এই token দিয়ে YouTube API call
    fetch(`https://www.googleapis.com/youtube/v3/subscriptions?mine=true`, {
      headers: {
        Authorization: `Bearer ${response.access_token}`
      }
    })
  }
})

client.requestAccessToken() // Popup আসবে, User Allow করবে
```

---

## 🖥️ Step 5: Backend Proxy (Optional but Recommended for Production)

Frontend এ API Key থাকলে যে কেউ দেখতে পারে। তাই Backend Proxy:

```javascript
// server/index.js - Express Server

// Frontend থেকে: /api/search?q=android
// Backend থেকে: https://www.googleapis.com/youtube/v3/search?key=HIDDEN_KEY&q=android

app.get('/api/search', async (req, res) => {
  const { q } = req.query
  
  const youtubeUrl = `https://www.googleapis.com/youtube/v3/search?
    key=${process.env.YOUTUBE_API_KEY} // .env থেকে, frontend এ যায় না
    &q=${q}
    &part=snippet`
  
  const response = await fetch(youtubeUrl)
  const data = await response.json()
  
  res.json(data) // Frontend এ পাঠানো
})
```

**Run:**
```bash
npm install express cors dotenv
node server/index.js
```

**Frontend .env:**
```env
VITE_API_BASE_URL=http://localhost:3001
```

---

## 📱 Step 6: Android এ কিভাবে?

### WebView (Easy)
```kotlin
webView.loadUrl("https://mytube.vercel.app")
// API Key backend এ থাকবে, Android থেকে backend call
```

### Native Retrofit (Pro)
```kotlin
// YouTubeApiService.kt
interface YouTubeApi {
  @GET("search")
  suspend fun search(
    @Query("q") query: String,
    @Query("key") apiKey: String = BuildConfig.YOUTUBE_API_KEY
  ): SearchResponse
}

// Google Sign-In
val gso = GoogleSignInOptions.Builder()
  .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
  .requestEmail()
  .build()
```

---

## 🧪 Test করুন

1. `.env` ফাইল তৈরি করুন (`.env.example` থেকে কপি)
2. API Key + Client ID বসান
3. `npm run dev`
4. Browser Console এ দেখুন:
   - `🔑 YouTube API Mode: REAL`
   - `🔐 Google Auth Mode: REAL`
   - `📡 YouTube API Call: search`

---

## 📚 Important Links

- YouTube Data API Docs: https://developers.google.com/youtube/v3/docs
- Quota Calculator: https://developers.google.com/youtube/v3/determine_quota_cost
- Google Identity Services: https://developers.google.com/identity/gsi/web
- OAuth 2.0 Playground: https://developers.google.com/oauthplayground/
- API Explorer: https://developers.google.com/youtube/v3/docs/search/list

---

## ❓ Common Errors

**1. API Key Invalid:**
- Check .env file, restart dev server
- Enable YouTube Data API v3

**2. Quota Exceeded:**
- দিনে 10k limit, কাল আবার try করুন
- Cache ব্যবহার করুন

**3. OAuth Origin Error:**
- Google Console -> Credentials -> Authorized JavaScript origins এ localhost:5173 add করুন

**4. CORS Error:**
- Backend proxy ব্যবহার করুন বা API Key restriction None করুন (Dev only)

---

**Happy Coding! 🚀 আমিন, এখন তুমি Real YouTube Clone বানাতে পারবে!**
