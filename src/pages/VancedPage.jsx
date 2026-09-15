import { Link } from 'react-router-dom';

export default function VancedPage() {
  return (
    <div className="min-h-[calc(100vh-56px)] bg-[#0f0f0f] p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-3 bg-[#212121] border border-[#303030] rounded-full px-6 py-2">
            <span className="bg-red-600 w-8 h-8 rounded-full flex items-center justify-center font-bold">V</span>
            <span className="font-bold">MyVanced</span>
            <span className="text-xs bg-green-900/30 text-green-400 px-2 py-1 rounded-full border border-green-800">Educational</span>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold mt-6">Vanced এর মতো YouTube Clone কিভাবে বানাবে?</h1>
          <p className="text-[#aaa] mt-3 max-w-2xl mx-auto">চাকরির জন্য না, শেখার মজার জন্য - Vanced এর ভিতরের সব সিক্রেট!</p>
        </div>

        {/* Comparison */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg">🔴 Real Vanced (YouTube ReVanced)</h3>
            <p className="text-xs text-[#717171] mt-1">APK Patching - Hacker Way</p>
            <ul className="mt-4 space-y-2 text-sm text-[#aaa]">
              <li className="flex gap-2"><span>🔧</span> Official YouTube APK Decompile (apktool)</li>
              <li className="flex gap-2"><span>💉</span> Smali Code Patch - showAds() → return-void</li>
              <li className="flex gap-2"><span>📦</span> Recompile + Sign + MicroG</li>
              <li className="flex gap-2"><span>🚫</span> AdBlock at bytecode level</li>
              <li className="flex gap-2"><span>⚠️</span> Grey Area - Play Store এ নেই</li>
            </ul>
            <div className="mt-4 bg-[#0f0f0f] p-3 rounded-xl text-xs font-mono text-[#717171]">
              apktool d youtube.apk<br/>
              # Edit smali/com/google/.../Ads.smali<br/>
              apktool b -o patched.apk
            </div>
          </div>

          <div className="bg-gradient-to-br from-red-900/20 to-[#212121] border border-red-800/30 rounded-2xl p-6">
            <h3 className="font-bold text-lg">🟢 MyVanced (Our Way)</h3>
            <p className="text-xs text-[#717171] mt-1">From Scratch - Educational & Legal</p>
            <ul className="mt-4 space-y-2 text-sm text-[#aaa]">
              <li className="flex gap-2"><span>🔍</span> NewPipeExtractor - YouTube Scraping (No API Key)</li>
              <li className="flex gap-2"><span>🎬</span> ExoPlayer - Custom Player</li>
              <li className="flex gap-2"><span>🎵</span> Foreground Service - Background Play</li>
              <li className="flex gap-2"><span>💰</span> SponsorBlock API - Auto Skip</li>
              <li className="flex gap-2"><span>✅</span> Legal & Open Source - NewPipe এর মতো</li>
            </ul>
            <div className="mt-4 bg-[#0f0f0f] p-3 rounded-xl text-xs font-mono text-[#717171]">
              implementation("com.github.TeamNewPipe:NewPipeExtractor")<br/>
              val extractor = YouTube.getStreamExtractor(url)<br/>
              extractor.fetchPage() // Direct video URL!
            </div>
          </div>
        </div>

        {/* Features Deep Dive */}
        <div className="space-y-6">
          <h2 className="text-2xl font-bold">🎯 Vanced এর 6 টা মেইন ফিচার কিভাবে কাজ করে?</h2>

          {/* AdBlock */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg flex items-center gap-2">🚫 1. AdBlock</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div>
                <p className="text-sm font-medium text-white">Vanced (APK Patch):</p>
                <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-2 text-xs overflow-x-auto">
{`// smali - Ads.smali
.method public shouldShowAd()Z
  const/4 v0, 0x0  # false
  return v0
.end method

// Network - Block Ad URLs
if (url.contains("googleadservices.com")) 
  return true // isAd`}
                </pre>
              </div>
              <div>
                <p className="text-sm font-medium text-white">MyVanced (Legal):</p>
                <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-2 text-xs overflow-x-auto">
{`// Service Worker (Web)
// sw.js - Block Ad Domains
if (url.hostname.includes("doubleclick.net") ||
    url.hostname.includes("googleadservices.com")) {
  return new Response("", {status: 204})
}

// Android ExoPlayer
player.addListener {
  if (isAdUrl(currentUrl)) skipAd()
}`}
                </pre>
              </div>
            </div>
          </div>

          {/* Background Play */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg flex items-center gap-2">🎵 2. Background Playback</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              <div>
                <p className="text-sm font-medium text-white">Vanced:</p>
                <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-2 text-xs overflow-x-auto">
{`// PlayerService.smali
.method onAppBackgrounded()V
  # Original: pauseVideo()
  # Patched: return-void (Don't pause!)
  return-void
.end method

// Foreground Service
startForeground(NOTIFICATION_ID, notification)
player.playWhenReady = true`}
                </pre>
              </div>
              <div>
                <p className="text-sm font-medium text-white">MyVanced Web:</p>
                <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-2 text-xs overflow-x-auto">
{`// Media Session API (Web)
navigator.mediaSession.metadata = 
  new MediaMetadata({title, artist})

navigator.mediaSession.setActionHandler('play', 
  () => video.play())

// Don't pause on visibilitychange
document.addEventListener('visibilitychange', () => {
  if (document.hidden) keepPlaying()
})`}
                </pre>
              </div>
            </div>
          </div>

          {/* SponsorBlock */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg flex items-center gap-2">💰 3. SponsorBlock - Auto Skip Sponsors</h3>
            <p className="text-sm text-[#aaa] mt-2">Community driven - ইউজাররা মার্ক করে কোথায় Sponsor, Intro, Outro আছে</p>
            <div className="mt-4 bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
              <p className="text-xs font-medium">API: https://sponsor.ajay.app/api/skipSegments?videoID=VIDEO_ID</p>
              <pre className="text-xs mt-2 overflow-x-auto">
{`// Response:
[
  {"segment": [0, 10], "category": "intro"},
  {"segment": [120, 150], "category": "sponsor"}
]

// Auto Skip Logic:
video.addEventListener('timeupdate', () => {
  for (seg of segments) {
    if (currentTime >= seg.start && currentTime < seg.end) {
      video.currentTime = seg.end // Skip!
    }
  }
})`}
              </pre>
              <p className="text-xs text-green-400 mt-2">✅ MyTube এ Already Integrated! Video Page এ দেখো</p>
            </div>
          </div>

          {/* Return Dislike */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg">👎 4. Return YouTube Dislike</h3>
            <p className="text-sm text-[#aaa] mt-2">YouTube 2021 এ Dislike hide করে দিয়েছিল, RYD ফিরিয়ে আনে</p>
            <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-3 text-xs">
{`API: https://returnyoutubedislikeapi.com/votes?videoId=VIDEO_ID
Response: {likes: 10000, dislikes: 500, rating: 4.5}

MyTube এ Integrated - Video Page এ Dislike count দেখতে পাবে!`}
            </pre>
          </div>

          {/* PiP */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg">📺 5. Picture-in-Picture (PiP)</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-3">
              <pre className="bg-[#0f0f0f] p-3 rounded-xl text-xs">
{`// Web PiP API
if (document.pictureInPictureEnabled) {
  await video.requestPictureInPicture()
}

// Android PiP
enterPictureInPictureMode(
  PictureInPictureParams.Builder().build()
)`}
              </pre>
              <div className="bg-[#0f0f0f] p-3 rounded-xl text-xs">
                <p className="font-medium">Try it:</p>
                <p className="text-[#aaa] mt-1">Video Page এ PiP বাটন চাপো → Floating Window!</p>
                <p className="text-[#717171] mt-2">Vanced এও Same - Android PiP Mode</p>
              </div>
            </div>
          </div>

          {/* MicroG */}
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
            <h3 className="font-bold text-lg">🔐 6. MicroG / GmsCore - Fake Google Play Service</h3>
            <p className="text-sm text-[#aaa] mt-2">Vanced এ Google Login কিভাবে কাজ করে? Real Google Play Service ছাড়া?</p>
            <pre className="bg-[#0f0f0f] p-3 rounded-xl mt-3 text-xs overflow-x-auto">
{`// Real YouTube App:
import com.google.android.gms.auth.GoogleAuthUtil
GoogleAuthUtil.getToken(context, account, scope) // Needs Real Play Service

// MicroG - Fake Same Package:
package com.google.android.gms.auth  // Same package!
class GoogleAuthUtil {
  fun getToken(...): String {
    // Fake, but actually does OAuth via WebView
    return realTokenFromWebView()
  }
}

// YouTube App thinks it's Real Play Service! Login works!`}
            </pre>
          </div>
        </div>

        {/* MyVanced Android Project */}
        <div className="mt-8 bg-gradient-to-br from-red-900/20 to-[#212121] border border-red-800/30 rounded-2xl p-6">
          <h2 className="text-xl font-bold">📱 MyVanced Android Project - তোমার জন্য Ready!</h2>
          <p className="text-sm text-[#aaa] mt-2">myvanced/android/ ফোল্ডারে Full Android App Structure আছে</p>
          
          <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
            <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
              <p className="font-bold">MainActivity.kt</p>
              <p className="text-[#717171] mt-1">Jetpack Compose UI + NewPipeExtractor Search</p>
            </div>
            <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
              <p className="font-bold">PlaybackService.kt</p>
              <p className="text-[#717171] mt-1">Foreground Service + ExoPlayer + SponsorBlock</p>
            </div>
            <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
              <p className="font-bold">YouTubeExtractor.kt</p>
              <p className="text-[#717171] mt-1">NewPipeExtractor - No API Key, Scraping</p>
            </div>
            <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
              <p className="font-bold">build.gradle</p>
              <p className="text-[#717171] mt-1">ExoPlayer + NewPipeExtractor + Compose dependencies</p>
            </div>
          </div>

          <div className="mt-6 flex gap-2">
            <Link to="/watch/dQw4w9WgXcQ" className="bg-red-600 text-white px-6 py-2 rounded-full font-medium text-sm">🎬 Vanced Features Test করুন</Link>
            <a href="https://github.com/TeamNewPipe/NewPipeExtractor" target="_blank" className="bg-[#303030] px-6 py-2 rounded-full text-sm">NewPipeExtractor Docs →</a>
          </div>
        </div>

        {/* Fun Learning */}
        <div className="mt-8 bg-[#212121] border border-[#303030] rounded-2xl p-6 text-center">
          <p className="text-2xl">🎓</p>
          <h3 className="font-bold mt-2">শেখার মজা এখানেই!</h3>
          <p className="text-sm text-[#aaa] mt-2 max-w-2xl mx-auto">
            Vanced বানানো মানে Reverse Engineering, Patching, Smali - এগুলো Security Researcher রা করে।
            MyVanced বানানো মানে NewPipeExtractor, ExoPlayer, Foreground Service - এগুলো Android Developer রা করে।
            দুটোই মজার, দুটোই শেখার মতো! তুমি যেটা মজা পাও সেটাই করো।
          </p>
          <div className="mt-4 flex justify-center gap-2">
            <Link to="/" className="bg-white text-black px-6 py-2 rounded-full font-medium text-sm">← হোমে ফিরুন</Link>
            <Link to="/install" className="bg-[#303030] px-6 py-2 rounded-full text-sm">📱 ফোনে ইন্সটল</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
