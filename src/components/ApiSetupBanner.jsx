import { useState } from 'react';
import { apiStatus } from '../services/youtubeApi';
import { authStatus } from '../services/googleAuth';

export default function ApiSetupBanner() {
  const [dismissed, setDismissed] = useState(() => localStorage.getItem('mytube_banner_dismissed') === 'true');
  const [showDetails, setShowDetails] = useState(false);

  if (dismissed || (apiStatus.hasApiKey && authStatus.hasClientId)) return null;

  return (
    <div className="bg-gradient-to-r from-[#1a1a1a] to-[#272727] border border-[#303030] rounded-xl m-4 p-4">
      <div className="flex items-start justify-between gap-4">
        <div className="flex gap-3 flex-1">
          <span className="text-2xl">🔑</span>
          <div className="flex-1">
            <h3 className="font-bold text-[15px]">ফুল ফাংশনাল মোড চালু করুন (Real YouTube API)</h3>
            <p className="text-sm text-[#aaa] mt-1">
              এখন Demo Mode চলছে। Real YouTube Data দিয়ে চালাতে নিচের Step গুলো ফলো করুন:
            </p>
            
            <div className="mt-3 flex flex-wrap gap-2">
              <span className={`px-2.5 py-1 rounded-full text-xs font-medium ${apiStatus.hasApiKey ? 'bg-green-900/30 text-green-400 border border-green-800' : 'bg-red-900/30 text-red-400 border border-red-800'}`}>
                {apiStatus.hasApiKey ? '✅ YouTube API: Connected' : '❌ YouTube API: Mock'}
              </span>
              <span className={`px-2.5 py-1 rounded-full text-xs font-medium ${authStatus.hasClientId ? 'bg-green-900/30 text-green-400 border border-green-800' : 'bg-red-900/30 text-red-400 border border-red-800'}`}>
                {authStatus.hasClientId ? '✅ Google OAuth: Connected' : '❌ Google OAuth: Mock'}
              </span>
            </div>

            <button 
              onClick={() => setShowDetails(!showDetails)}
              className="mt-3 text-sm text-[#3ea6ff] hover:underline"
            >
              {showDetails ? '▲ বিস্তারিত লুকান' : '▼ কিভাবে Real API কানেক্ট করবেন? (বাংলায় গাইড)'}
            </button>

            {showDetails && (
              <div className="mt-4 bg-[#0f0f0f] rounded-lg p-4 text-sm space-y-4 border border-[#303030]">
                <div>
                  <h4 className="font-bold text-white">Step 1: Google Cloud Project তৈরি</h4>
                  <ol className="list-decimal list-inside mt-2 space-y-1 text-[#aaa] text-[13px]">
                    <li><a href="https://console.cloud.google.com/" target="_blank" className="text-[#3ea6ff] underline">console.cloud.google.com</a> এ যান</li>
                    <li>New Project → নাম দিন: MyTube</li>
                    <li>APIs & Services → Library → YouTube Data API v3 → Enable</li>
                  </ol>
                </div>

                <div>
                  <h4 className="font-bold text-white">Step 2: API Key তৈরি (YouTube Data এর জন্য)</h4>
                  <ol className="list-decimal list-inside mt-2 space-y-1 text-[#aaa] text-[13px]">
                    <li>Credentials → Create Credentials → API Key</li>
                    <li>API Key কপি করুন</li>
                    <li>Restrict Key → YouTube Data API v3 সিলেক্ট করুন</li>
                    <li><code className="bg-[#272727] px-1.5 py-0.5 rounded text-white">.env</code> ফাইলে <code className="bg-[#272727] px-1.5 py-0.5 rounded text-white">VITE_YOUTUBE_API_KEY=আপনার_KEY</code></li>
                  </ol>
                  <div className="mt-2 bg-[#272727] p-2.5 rounded text-xs font-mono text-[#aaa]">
                    <div>youtubeApi.js এ কিভাবে কথা বলে:</div>
                    <div className="text-white mt-1">fetch(`https://www.googleapis.com/youtube/v3/search?part=snippet&amp;q=android&amp;key=${'{API_KEY}'}`)</div>
                  </div>
                </div>

                <div>
                  <h4 className="font-bold text-white">Step 3: OAuth Client ID (Google Login এর জন্য)</h4>
                  <ol className="list-decimal list-inside mt-2 space-y-1 text-[#aaa] text-[13px]">
                    <li>Credentials → Create Credentials → OAuth Client ID</li>
                    <li>Application Type: Web Application</li>
                    <li>Authorized JavaScript origins: <code className="bg-[#272727] px-1 py-0.5 rounded">http://localhost:5173</code></li>
                    <li>Client ID কপি করে <code className="bg-[#272727] px-1 py-0.5 rounded text-white">VITE_GOOGLE_CLIENT_ID</code> এ বসান</li>
                  </ol>
                  <div className="mt-2 bg-[#272727] p-2.5 rounded text-xs font-mono text-[#aaa]">
                    <div>googleAuth.js এ কিভাবে কথা বলে:</div>
                    <div className="text-white mt-1">google.accounts.id.initialize({'{'} client_id: CLIENT_ID, callback: handleLogin {'}'})</div>
                  </div>
                </div>

                <div className="bg-blue-900/20 border border-blue-800/50 p-3 rounded-lg">
                  <p className="text-blue-300 font-medium text-xs">💡 শেখার জন্য গুরুত্বপূর্ণ:</p>
                  <ul className="list-disc list-inside mt-1 text-[12px] text-blue-200/70 space-y-1">
                    <li><strong>API Key</strong> = Public Data (ভিডিও সার্চ, Trending) - সবার জন্য</li>
                    <li><strong>OAuth Token</strong> = Private Data (Like, Subscribe, History) - শুধু লগইন করা ইউজারের</li>
                    <li>Quota: দিনে 10,000 unit ফ্রি (Search = 100 unit, Video Details = 1 unit)</li>
                    <li>কোড দেখো: <code className="bg-black/30 px-1 rounded">src/services/youtubeApi.js</code> এবং <code className="bg-black/30 px-1 rounded">googleAuth.js</code></li>
                  </ul>
                </div>

                <div className="flex gap-2">
                  <a href="https://developers.google.com/youtube/v3/getting-started" target="_blank" className="bg-[#272727] hover:bg-[#3f3f3f] px-3 py-1.5 rounded-full text-xs">📚 YouTube API Docs</a>
                  <a href="https://developers.google.com/identity/gsi/web" target="_blank" className="bg-[#272727] hover:bg-[#3f3f3f] px-3 py-1.5 rounded-full text-xs">🔐 Google Auth Docs</a>
                </div>
              </div>
            )}
          </div>
        </div>
        <button onClick={() => { setDismissed(true); localStorage.setItem('mytube_banner_dismissed', 'true'); }} className="text-[#717171] hover:text-white p-1">✕</button>
      </div>
    </div>
  );
}
