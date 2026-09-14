import { useState } from 'react';
import { apiStatus } from '../services/youtubeApi';
import { authStatus } from '../services/googleAuth';
import { searchVideos, getPopularVideos, getVideoDetails } from '../services/youtubeApi';

export default function ApiDebugger() {
  const [query, setQuery] = useState('android development');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('search');

  const runTest = async () => {
    setLoading(true);
    setResult(null);
    try {
      let data;
      if (activeTab === 'search') {
        data = await searchVideos(query);
      } else if (activeTab === 'popular') {
        data = await getPopularVideos();
      } else if (activeTab === 'video') {
        data = await getVideoDetails(query || 'dQw4w9WgXcQ');
      }
      setResult(data);
    } catch (e) {
      setResult({ error: e.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 bg-[#0f0f0f] min-h-[calc(100vh-56px)]">
      <h1 className="text-2xl font-bold mb-2">🔧 API Debugger - আমিনের জন্য Learning Lab</h1>
      <p className="text-[#aaa] text-sm mb-6">এখানে দেখতে পারবে কিভাবে Google API এর সাথে কথা হয়, Raw Response কি আসে</p>

      {/* Status */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <div className="bg-[#212121] border border-[#303030] rounded-xl p-4">
          <h3 className="font-bold flex items-center gap-2">
            <span className={`w-3 h-3 rounded-full ${apiStatus.hasApiKey ? 'bg-green-500' : 'bg-yellow-500'}`}></span>
            YouTube Data API v3
          </h3>
          <div className="mt-3 space-y-2 text-sm font-mono">
            <p><span className="text-[#717171]">Mode:</span> <span className={apiStatus.isMock ? 'text-yellow-400' : 'text-green-400'}>{apiStatus.isMock ? 'MOCK (Demo)' : 'REAL'}</span></p>
            <p><span className="text-[#717171]">Key:</span> {apiStatus.apiKey}</p>
            <p><span className="text-[#717171]">Base URL:</span> https://www.googleapis.com/youtube/v3</p>
            <p className="text-xs text-[#717171] mt-2">Quota: Search=100 units, Video=1 unit, Daily limit 10,000</p>
          </div>
        </div>

        <div className="bg-[#212121] border border-[#303030] rounded-xl p-4">
          <h3 className="font-bold flex items-center gap-2">
            <span className={`w-3 h-3 rounded-full ${authStatus.hasClientId ? 'bg-green-500' : 'bg-yellow-500'}`}></span>
            Google OAuth 2.0
          </h3>
          <div className="mt-3 space-y-2 text-sm font-mono">
            <p><span className="text-[#717171]">Mode:</span> <span className={authStatus.isMock ? 'text-yellow-400' : 'text-green-400'}>{authStatus.isMock ? 'MOCK' : 'REAL (GSI)'}</span></p>
            <p><span className="text-[#717171]">Client ID:</span> {authStatus.clientId}</p>
            <p><span className="text-[#717171]">Library:</span> https://accounts.google.com/gsi/client</p>
            <p className="text-xs text-[#717171] mt-2">Scopes: youtube.readonly, youtube.force-ssl, userinfo</p>
          </div>
        </div>
      </div>

      {/* Tester */}
      <div className="bg-[#212121] border border-[#303030] rounded-xl p-4 mb-6">
        <div className="flex gap-2 mb-4">
          <button onClick={() => setActiveTab('search')} className={`px-4 py-2 rounded-full text-sm ${activeTab === 'search' ? 'bg-white text-black' : 'bg-[#303030]'}`}>Search API</button>
          <button onClick={() => setActiveTab('popular')} className={`px-4 py-2 rounded-full text-sm ${activeTab === 'popular' ? 'bg-white text-black' : 'bg-[#303030]'}`}>Popular API</button>
          <button onClick={() => setActiveTab('video')} className={`px-4 py-2 rounded-full text-sm ${activeTab === 'video' ? 'bg-white text-black' : 'bg-[#303030]'}`}>Video Details</button>
        </div>

        <div className="flex gap-2">
          <input
            value={query}
            onChange={e => setQuery(e.target.value)}
            placeholder={activeTab === 'video' ? 'Video ID (e.g. dQw4w9WgXcQ)' : 'Search query'}
            className="flex-1 bg-[#0f0f0f] border border-[#303030] rounded-full px-4 py-2 text-sm focus:outline-none focus:border-[#3ea6ff]"
          />
          <button onClick={runTest} disabled={loading} className="bg-[#3ea6ff] text-black px-6 py-2 rounded-full text-sm font-medium disabled:opacity-50">
            {loading ? 'Calling...' : 'Call API 🚀'}
          </button>
        </div>

        <div className="mt-4 bg-[#0f0f0f] rounded-lg p-3 border border-[#303030]">
          <p className="text-xs text-[#717171] font-medium">Code that runs:</p>
          <pre className="text-xs text-[#aaa] mt-2 overflow-x-auto">
{activeTab === 'search' ? `// youtubeApi.js
const url = new URL('https://www.googleapis.com/youtube/v3/search')
url.searchParams.set('key', API_KEY)
url.searchParams.set('part', 'snippet')
url.searchParams.set('q', '${query}')
url.searchParams.set('type', 'video')

const res = await fetch(url)
const data = await res.json()
// data.items -> video list` : activeTab === 'popular' ? `// Popular videos
fetch('https://www.googleapis.com/youtube/v3/videos?chart=mostPopular&regionCode=BD&key='+API_KEY)` : `// Video details
fetch('https://www.googleapis.com/youtube/v3/videos?id=${query}&part=statistics,snippet,contentDetails&key='+API_KEY)`}
          </pre>
        </div>
      </div>

      {/* Result */}
      {result && (
        <div className="bg-[#212121] border border-[#303030] rounded-xl p-4">
          <h3 className="font-bold mb-3">📦 API Response:</h3>
          <div className="bg-[#0f0f0f] rounded-lg p-4 overflow-auto max-h-[500px] border border-[#303030]">
            <pre className="text-xs text-[#aaa] whitespace-pre-wrap">
              {JSON.stringify(result, null, 2)}
            </pre>
          </div>
          <div className="mt-3 text-xs text-[#717171]">
            {result.isMock ? '⚠️ This is mock data. Add real API key for live data.' : '✅ Real YouTube API data!'}
          </div>
        </div>
      )}

      {/* Learning */}
      <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-gradient-to-br from-blue-900/20 to-[#212121] border border-blue-800/30 rounded-xl p-4">
          <h4 className="font-bold text-blue-300">📚 কিভাবে শিখবে?</h4>
          <ol className="list-decimal list-inside mt-2 text-sm text-[#aaa] space-y-1">
            <li>Network Tab খুলো (F12), Search করো, API Call দেখো</li>
            <li>youtubeApi.js এর প্রতিটা function console.log দেখো</li>
            <li>Quota কিভাবে কাজ করে - Google Cloud Console এ Metrics দেখো</li>
            <li>Backend proxy কিভাবে API Key hide করে - server/index.js দেখো</li>
          </ol>
        </div>
        <div className="bg-gradient-to-br from-green-900/20 to-[#212121] border border-green-800/30 rounded-xl p-4">
          <h4 className="font-bold text-green-300">🤖 Android এ Same Logic</h4>
          <pre className="text-xs text-[#aaa] mt-2 whitespace-pre-wrap">
{`// Retrofit - Same as fetch()
interface YouTubeApi {
  @GET("search")
  suspend fun search(
    @Query("q") q: String,
    @Query("key") key: String
  ): SearchResponse
}

// Google Sign-In - Same JWT
val credential = GoogleAuthProvider
  .getCredential(idToken, null)`}
          </pre>
        </div>
      </div>
    </div>
  );
}
