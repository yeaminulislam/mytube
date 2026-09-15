import { useState } from 'react';
import { usePWAInstall } from '../hooks/usePWAInstall';
import { Link } from 'react-router-dom';

export default function InstallPage() {
  const { isInstallable, isInstalled, installApp } = usePWAInstall();
  const [activeMethod, setActiveMethod] = useState('pwa');

  return (
    <div className="min-h-[calc(100vh-56px)] bg-[#0f0f0f] p-4 md:p-8">
      <div className="max-w-4xl mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold">📱 MyTube ফোনে ইন্সটল করুন</h1>
          <p className="text-[#aaa] mt-2">৫টি উপায়ে - সবচেয়ে সহজ থেকে প্রফেশনাল</p>
        </div>

        {/* Method Tabs */}
        <div className="flex gap-2 overflow-x-auto scrollbar-hide mb-6 bg-[#212121] p-2 rounded-xl w-fit mx-auto">
          <button onClick={() => setActiveMethod('pwa')} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap ${activeMethod === 'pwa' ? 'bg-white text-black' : 'bg-[#303030] text-white hover:bg-[#3f3f3f]'}`}>⚡ 1-Click PWA (সহজ)</button>
          <button onClick={() => setActiveMethod('apk')} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap ${activeMethod === 'apk' ? 'bg-white text-black' : 'bg-[#303030] text-white hover:bg-[#3f3f3f]'}`}>📦 APK (Android Studio)</button>
          <button onClick={() => setActiveMethod('capacitor')} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap ${activeMethod === 'capacitor' ? 'bg-white text-black' : 'bg-[#303030] text-white hover:bg-[#3f3f3f]'}`}>🔋 Capacitor</button>
          <button onClick={() => setActiveMethod('twa')} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap ${activeMethod === 'twa' ? 'bg-white text-black' : 'bg-[#303030] text-white hover:bg-[#3f3f3f]'}`}>🏪 Play Store</button>
          <button onClick={() => setActiveMethod('video')} className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap ${activeMethod === 'video' ? 'bg-white text-black' : 'bg-[#303030] text-white hover:bg-[#3f3f3f]'}`}>🎥 ভিডিও গাইড</button>
        </div>

        {/* PWA - Easiest */}
        {activeMethod === 'pwa' && (
          <div className="space-y-6">
            <div className="bg-gradient-to-br from-red-600 to-red-800 rounded-2xl p-6 md:p-8 text-white">
              <div className="flex items-start gap-4">
                <div className="bg-white text-red-600 w-12 h-12 rounded-xl flex items-center justify-center text-2xl font-bold">1</div>
                <div className="flex-1">
                  <h2 className="text-2xl font-bold">PWA Install - 10 সেকেন্ডে (Recommended for now)</h2>
                  <p className="text-red-100 mt-2">কোনো কোড লাগবে না, APK লাগবে না, Play Store লাগবে না! Chrome থেকে সরাসরি App হিসেবে ইন্সটল</p>
                  
                  <div className="mt-6 bg-white/10 backdrop-blur rounded-xl p-4">
                    <h3 className="font-bold">🤖 Android Chrome এ:</h3>
                    <ol className="list-decimal list-inside mt-3 space-y-2 text-sm">
                      <li>এই ওয়েবসাইট Chrome এ ওপেন করুন (যে লিংকে আছেন)</li>
                      <li>নিচে লাল Banner আসবে → <strong>"📲 ইন্সটল করুন"</strong> চাপুন</li>
                      <li>অথবা Chrome Menu (⋮) → <strong>"Add to Home screen"</strong> বা <strong>"Install App"</strong></li>
                      <li>Install চাপুন → হোম স্ক্রিনে MyTube আইকন আসবে!</li>
                      <li>আইকনে চাপুন → YouTube এর মতো Fullscreen App খুলবে!</li>
                    </ol>
                    
                    <div className="mt-4 flex gap-2">
                      {isInstalled ? (
                        <div className="bg-green-500 text-white px-6 py-3 rounded-full font-bold">✅ ইন্সটল করা আছে!</div>
                      ) : isInstallable ? (
                        <button onClick={installApp} className="bg-white text-red-600 px-8 py-3 rounded-full font-bold text-lg hover:bg-red-50 animate-pulse">
                          📲 এখনই ইন্সটল করুন
                        </button>
                      ) : (
                        <div className="bg-black/20 px-6 py-3 rounded-full text-sm">
                          Chrome Menu (⋮) → Install App চাপুন
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="mt-4 bg-black/20 rounded-xl p-4">
                    <h4 className="font-bold text-sm">🍎 iPhone Safari তে:</h4>
                    <ol className="list-decimal list-inside mt-2 space-y-1 text-sm text-red-100">
                      <li>Share বাটন (⎙) → Add to Home Screen → Add</li>
                    </ol>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-[#212121] border border-[#303030] rounded-xl p-6">
              <h3 className="font-bold">💡 PWA কেন ভালো?</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4 text-sm">
                <div className="bg-[#181818] p-4 rounded-xl">
                  <p className="text-2xl">⚡</p>
                  <p className="font-bold mt-2">Super Fast</p>
                  <p className="text-[#aaa] text-xs mt-1">Offline কাজ করে, YouTube API ছাড়া বাকি সব cache</p>
                </div>
                <div className="bg-[#181818] p-4 rounded-xl">
                  <p className="text-2xl">📦</p>
                  <p className="font-bold mt-2">No APK</p>
                  <p className="text-[#aaa] text-xs mt-1">1MB এরও কম, ফোন মেমরি বাঁচে</p>
                </div>
                <div className="bg-[#181818] p-4 rounded-xl">
                  <p className="text-2xl">🔄</p>
                  <p className="font-bold mt-2">Auto Update</p>
                  <p className="text-[#aaa] text-xs mt-1">নতুন ভার্সন দিলে auto update, Play Store লাগে না</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* APK via Android Studio */}
        {activeMethod === 'apk' && (
          <div className="space-y-6">
            <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
              <h2 className="text-xl font-bold flex items-center gap-2">
                <span className="bg-blue-600 w-8 h-8 rounded-full flex items-center justify-center text-sm">2</span>
                WebView APK - Android Studio দিয়ে (30 মিনিট)
              </h2>
              
              <div className="mt-6 space-y-4">
                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 1: Android Studio Install</h4>
                  <p className="text-xs text-[#aaa] mt-1">https://developer.android.com/studio থেকে Download করুন</p>
                </div>

                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 2: New Project</h4>
                  <pre className="text-xs bg-[#181818] p-3 rounded mt-2 overflow-x-auto text-[#aaa]">
{`Android Studio → New Project → Empty Activity
Name: MyTube
Package: com.amin.mytube
Language: Kotlin
Minimum SDK: API 21`}
                  </pre>
                </div>

                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 3: AndroidManifest.xml এ Permission</h4>
                  <pre className="text-xs bg-[#181818] p-3 rounded mt-2 overflow-x-auto">
{`<manifest>
  <uses-permission android:name="android.permission.INTERNET" />
  <application
    android:usesCleartextTraffic="true"
    ...>
  </application>
</manifest>`}
                  </pre>
                </div>

                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 4: activity_main.xml</h4>
                  <pre className="text-xs bg-[#181818] p-3 rounded mt-2 overflow-x-auto">
{`<WebView
  android:id="@+id/webView"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />`}
                  </pre>
                </div>

                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 5: MainActivity.kt (android-guide/MainActivity.kt থেকে কপি)</h4>
                  <pre className="text-xs bg-[#181818] p-3 rounded mt-2 overflow-x-auto">
{`class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val webView: WebView = findViewById(R.id.webView)
    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    webView.webViewClient = WebViewClient()
    webView.loadUrl("https://your-mytube-url.vercel.app")
  }
}`}
                  </pre>
                </div>

                <div className="bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                  <h4 className="font-bold text-sm">Step 6: APK Build</h4>
                  <pre className="text-xs bg-[#181818] p-3 rounded mt-2 overflow-x-auto">
{`Build → Build Bundle(s) / APK(s) → Build APK(s)
→ app/build/outputs/apk/debug/app-debug.apk
→ ফোনে কপি করে Install করুন!`}
                  </pre>
                </div>
              </div>

              <div className="mt-4 p-3 bg-blue-900/20 border border-blue-800/50 rounded-xl text-xs text-blue-300">
                💡 এই ফাইলগুলো `android-guide/` ফোল্ডারে রেডি আছে! শুধু কপি-পেস্ট করুন
              </div>
            </div>
          </div>
        )}

        {/* Capacitor */}
        {activeMethod === 'capacitor' && (
          <div className="space-y-6">
            <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
              <h2 className="text-xl font-bold">🔋 Capacitor - Modern Way (Best for Production)</h2>
              <p className="text-sm text-[#aaa] mt-2">এক কোডে Web + Android + iOS, Native Features (Camera, Push Notification) যোগ করা যায়</p>

              <div className="mt-6 bg-[#0f0f0f] rounded-xl p-4 border border-[#303030]">
                <pre className="text-xs overflow-x-auto">
{`# 1. Install Capacitor
npm install @capacitor/core @capacitor/cli @capacitor/android

# 2. Init
npx cap init MyTube com.amin.mytube --web-dir=dist

# 3. Build Web
npm run build

# 4. Add Android
npx cap add android

# 5. Sync
npx cap sync

# 6. Open Android Studio
npx cap open android

# 7. Run on Phone (USB Debugging ON)
# Android Studio → Run Button → Select Device

# 8. Build APK
# Build → Generate Signed Bundle / APK
`}
                </pre>
              </div>

              <div className="mt-4 bg-green-900/20 border border-green-800/50 rounded-xl p-4 text-sm">
                <p className="font-bold text-green-400">✅ Capacitor এর সুবিধা:</p>
                <ul className="list-disc list-inside mt-2 text-xs text-green-300/80 space-y-1">
                  <li>PWA এর সব সুবিধা + Native APK</li>
                  <li>Push Notification, Camera, File System access</li>
                  <li>Play Store এ Publish করা যায়</li>
                  <li>Live Reload - Code change করলে ফোনে auto update</li>
                </ul>
              </div>
            </div>
          </div>
        )}

        {/* TWA / Play Store */}
        {activeMethod === 'twa' && (
          <div className="space-y-6">
            <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6">
              <h2 className="text-xl font-bold">🏪 Play Store এ Publish (TWA - Trusted Web Activity)</h2>
              
              <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
                  <h4 className="font-bold text-sm">Option A: PWABuilder (Easy - No Code)</h4>
                  <ol className="list-decimal list-inside text-xs text-[#aaa] mt-2 space-y-1">
                    <li>https://www.pwabuilder.com/ এ যান</li>
                    <li>আপনার MyTube URL দিন (Vercel URL)</li>
                    <li>Analyze → Package for Store → Android</li>
                    <li>APK/AAB Download → Play Console এ Upload</li>
                  </ol>
                  <a href="https://www.pwabuilder.com/" target="_blank" className="mt-3 inline-block bg-[#303030] px-3 py-1.5 rounded-full text-xs">PWABuilder খুলুন →</a>
                </div>

                <div className="bg-[#0f0f0f] p-4 rounded-xl border border-[#303030]">
                  <h4 className="font-bold text-sm">Option B: Bubblewrap (Google Official)</h4>
                  <pre className="text-[11px] bg-[#181818] p-2 rounded mt-2 overflow-x-auto">
{`npm install -g @bubblewrap/cli
bubblewrap init --manifest https://your-site.com/manifest.json
bubblewrap build
# → app-release-signed.apk`}
                  </pre>
                </div>
              </div>

              <div className="mt-6 bg-yellow-900/20 border border-yellow-800/50 rounded-xl p-4 text-xs">
                <p className="font-bold text-yellow-400">💰 Play Store Publish করতে:</p>
                <ul className="list-disc list-inside mt-1 text-yellow-200/70 space-y-1">
                  <li>Google Play Console Account: $25 one-time</li>
                  <li>App Icon 512x512, Screenshots, Privacy Policy লাগবে</li>
                  <li>Package Name: com.amin.mytube (unique)</li>
                  <li>Review 1-3 দিন</li>
                </ul>
              </div>
            </div>
          </div>
        )}

        {/* Video Guide */}
        {activeMethod === 'video' && (
          <div className="bg-[#212121] border border-[#303030] rounded-2xl p-6 text-center">
            <p className="text-6xl">🎥</p>
            <h3 className="font-bold mt-4">ভিডিও গাইড শীঘ্রই আসছে</h3>
            <p className="text-sm text-[#aaa] mt-2">আপাতত উপরের লিখিত গাইড ফলো করুন</p>
            <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-3 text-left">
              <div className="bg-[#0f0f0f] p-3 rounded-xl border border-[#303030]">
                <p className="font-bold text-sm">1. PWA Install</p>
                <p className="text-xs text-[#717171] mt-1">YouTube এ "PWA install android" সার্চ করুন</p>
              </div>
              <div className="bg-[#0f0f0f] p-3 rounded-xl border border-[#303030]">
                <p className="font-bold text-sm">2. WebView APK</p>
                <p className="text-xs text-[#717171] mt-1">"WebView Android Studio" সার্চ করুন</p>
              </div>
              <div className="bg-[#0f0f0f] p-3 rounded-xl border border-[#303030]">
                <p className="font-bold text-sm">3. Capacitor</p>
                <p className="text-xs text-[#717171] mt-1">Capacitorjs.com/docs দেখুন</p>
              </div>
            </div>
          </div>
        )}

        {/* Quick Actions */}
        <div className="mt-8 bg-gradient-to-r from-[#1a1a1a] to-[#272727] rounded-2xl p-6 border border-[#303030]">
          <h3 className="font-bold">🚀 এখনই কি করবেন?</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-4">
            <div className="bg-[#0f0f0f] p-4 rounded-xl">
              <p className="font-bold text-sm">আজকের জন্য (2 মিনিট)</p>
              <p className="text-xs text-[#aaa] mt-1">Chrome থেকে PWA Install করুন → হোম স্ক্রিনে App পাবেন</p>
              <Link to="/" className="mt-3 inline-block bg-white text-black px-4 py-2 rounded-full text-xs font-bold">হোমে গিয়ে ইন্সটল করুন →</Link>
            </div>
            <div className="bg-[#0f0f0f] p-4 rounded-xl">
              <p className="font-bold text-sm">এই সপ্তাহে (1 ঘন্টা)</p>
              <p className="text-xs text-[#aaa] mt-1">Android Studio দিয়ে APK বানান → বন্ধুদের শেয়ার করুন</p>
              <a href="https://developer.android.com/studio" target="_blank" className="mt-3 inline-block bg-[#303030] px-4 py-2 rounded-full text-xs">Android Studio Download →</a>
            </div>
          </div>
        </div>

        <div className="text-center mt-8">
          <Link to="/" className="text-sm text-[#717171] hover:text-white">← হোমে ফিরে যান</Link>
        </div>
      </div>
    </div>
  );
}
