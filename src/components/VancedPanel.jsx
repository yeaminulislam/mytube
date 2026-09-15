import { useState } from 'react';

/**
 * Vanced Control Panel - Vanced এর সব Settings এক জায়গায়
 */

export default function VancedPanel({ vanced }) {
  const [showDetails, setShowDetails] = useState(false);

  if (!vanced) return null;

  const {
    sponsorSegments,
    dislikeData,
    isBackgroundPlayEnabled,
    isAmoledEnabled,
    isAdBlockEnabled,
    toggleBackgroundPlay,
    toggleAmoled,
    setIsAdBlockEnabled,
    enablePiP
  } = vanced;

  return (
    <div className="bg-[#1a1a1a] border border-[#303030] rounded-xl p-4 mt-4">
      <div className="flex items-center justify-between">
        <h3 className="font-bold flex items-center gap-2">
          <span className="bg-red-600 w-6 h-6 rounded-full flex items-center justify-center text-xs">V</span>
          MyVanced Features
          <span className="text-xs bg-[#303030] px-2 py-1 rounded-full font-normal">Vanced Style</span>
        </h3>
        <button onClick={() => setShowDetails(!showDetails)} className="text-xs text-[#3ea6ff] hover:underline">
          {showDetails ? '▲ Hide' : '▼ How it works?'}
        </button>
      </div>

      {/* Toggles - Like Vanced Settings */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-4">
        <div className="flex items-center justify-between bg-[#212121] p-3 rounded-xl border border-[#303030]">
          <div>
            <p className="font-medium text-sm">🚫 AdBlock</p>
            <p className="text-xs text-[#aaa]">YouTube Ads ব্লক (Simulated)</p>
          </div>
          <button
            onClick={() => setIsAdBlockEnabled(!isAdBlockEnabled)}
            className={`w-12 h-6 rounded-full relative transition-colors ${isAdBlockEnabled ? 'bg-red-600' : 'bg-[#3f3f3f]'}`}
          >
            <span className={`absolute top-0.5 w-5 h-5 bg-white rounded-full transition-transform ${isAdBlockEnabled ? 'translate-x-6' : 'translate-x-0.5'}`}></span>
          </button>
        </div>

        <div className="flex items-center justify-between bg-[#212121] p-3 rounded-xl border border-[#303030]">
          <div>
            <p className="font-medium text-sm">🎵 Background Play</p>
            <p className="text-xs text-[#aaa]">Tab hide করলেও চলবে</p>
          </div>
          <button
            onClick={toggleBackgroundPlay}
            className={`w-12 h-6 rounded-full relative transition-colors ${isBackgroundPlayEnabled ? 'bg-red-600' : 'bg-[#3f3f3f]'}`}
          >
            <span className={`absolute top-0.5 w-5 h-5 bg-white rounded-full transition-transform ${isBackgroundPlayEnabled ? 'translate-x-6' : 'translate-x-0.5'}`}></span>
          </button>
        </div>

        <div className="flex items-center justify-between bg-[#212121] p-3 rounded-xl border border-[#303030]">
          <div>
            <p className="font-medium text-sm">⚫ AMOLED Black</p>
            <p className="text-xs text-[#aaa]">True black theme</p>
          </div>
          <button
            onClick={toggleAmoled}
            className={`w-12 h-6 rounded-full relative transition-colors ${isAmoledEnabled ? 'bg-red-600' : 'bg-[#3f3f3f]'}`}
          >
            <span className={`absolute top-0.5 w-5 h-5 bg-white rounded-full transition-transform ${isAmoledEnabled ? 'translate-x-6' : 'translate-x-0.5'}`}></span>
          </button>
        </div>

        <button
          onClick={enablePiP}
          className="flex items-center justify-between bg-[#212121] p-3 rounded-xl border border-[#303030] hover:bg-[#272727] text-left"
        >
          <div>
            <p className="font-medium text-sm">📺 Picture-in-Picture</p>
            <p className="text-xs text-[#aaa]">Floating video</p>
          </div>
          <span className="text-xl">↗️</span>
        </button>
      </div>

      {/* SponsorBlock Status */}
      <div className="mt-4 bg-[#212121] p-3 rounded-xl border border-[#303030]">
        <p className="font-medium text-sm flex items-center gap-2">
          💰 SponsorBlock
          {sponsorSegments.length > 0 && <span className="bg-green-900/30 text-green-400 px-2 py-0.5 rounded-full text-xs border border-green-800">{sponsorSegments.length} segments</span>}
        </p>
        {sponsorSegments.length > 0 ? (
          <div className="mt-2 space-y-1">
            {sponsorSegments.map((seg, i) => (
              <div key={i} className="flex items-center gap-2 text-xs bg-[#0f0f0f] p-2 rounded-lg">
                <span className="bg-[#303030] px-2 py-0.5 rounded-full">{seg.category}</span>
                <span className="text-[#aaa]">{Math.floor(seg.start)}s → {Math.floor(seg.end)}s</span>
                <span className="text-green-400 ml-auto">Auto Skip ✓</span>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-xs text-[#717171] mt-1">এই ভিডিওতে কোনো Sponsor নেই, বা Community এখনো মার্ক করেনি</p>
        )}
        <p className="text-[11px] text-[#717171] mt-2">API: sponsor.ajay.app - Community driven, Vanced এভাবেই কাজ করে</p>
      </div>

      {/* Return Dislike */}
      {dislikeData && (
        <div className="mt-3 bg-[#212121] p-3 rounded-xl border border-[#303030]">
          <p className="font-medium text-sm">👎 Return YouTube Dislike</p>
          <div className="mt-2 flex items-center gap-4 text-xs">
            <span className="flex items-center gap-1"><span className="text-green-400">👍</span> {dislikeData.likes?.toLocaleString()}</span>
            <span className="flex items-center gap-1"><span className="text-red-400">👎</span> {dislikeData.dislikes?.toLocaleString()}</span>
            <span className="text-[#717171]">Rating: {dislikeData.rating?.toFixed(1)}/5</span>
          </div>
          <div className="mt-2 w-full bg-[#3f3f3f] h-1 rounded-full overflow-hidden flex">
            <div className="bg-[#3ea6ff] h-full" style={{ width: `${(dislikeData.likes / (dislikeData.likes + dislikeData.dislikes)) * 100}%` }}></div>
            <div className="bg-red-600 h-full" style={{ width: `${(dislikeData.dislikes / (dislikeData.likes + dislikeData.dislikes)) * 100}%` }}></div>
          </div>
          <p className="text-[11px] text-[#717171] mt-2">YouTube Dislike hide করে দিয়েছিল, RYD API ফিরিয়ে আনে - Vanced এটা use করে</p>
        </div>
      )}

      {/* How Vanced Works */}
      {showDetails && (
        <div className="mt-4 bg-[#0f0f0f] rounded-xl p-4 border border-[#303030] space-y-4 text-xs">
          <div>
            <h4 className="font-bold text-white">🔧 Vanced কিভাবে কাজ করে? (Real Technique)</h4>
            <div className="mt-2 space-y-2 text-[#aaa]">
              <p><strong className="text-white">1. APK Patching:</strong> YouTube APK → apktool দিয়ে Decompile → smali code edit → Recompile</p>
              <pre className="bg-[#181818] p-2 rounded text-[11px] overflow-x-auto">
{`# Vanced Patch Example (smali):
# Original:
invoke-virtual {v0}, Lcom/google/android/libraries/youtube/common/ui/FakeCard;->showAds()V

# Patched:
# return-void (Ad show না করে return)
`}
              </pre>
              <p><strong className="text-white">2. AdBlock:</strong> Ad URL গুলো Block: `googleadservices.com, doubleclick.net` → Service Worker বা Hosts file</p>
              <p><strong className="text-white">3. Background Play:</strong> Android Foreground Service + ExoPlayer → Notification এ Controls</p>
              <pre className="bg-[#181818] p-2 rounded text-[11px] overflow-x-auto">
{`// Android Foreground Service
class PlaybackService : Service() {
  override fun onStartCommand(intent: Intent, ...) {
    val player = ExoPlayer.Builder(this).build()
    player.playWhenReady = true
    startForeground(NOTIFICATION_ID, notification)
  }
}`}
              </pre>
              <p><strong className="text-white">4. SponsorBlock:</strong> Community API → Video time track করে auto skip</p>
              <p><strong className="text-white">5. MicroG:</strong> Google Play Service Fake করে → YouTube মনে করে Real Google Service আছে → Login কাজ করে</p>
            </div>
          </div>

          <div className="bg-blue-900/20 border border-blue-800/50 p-3 rounded-xl">
            <p className="font-bold text-blue-300">🎓 তোমার MyVanced (Legal Way):</p>
            <ul className="list-disc list-inside mt-2 text-blue-200/70 space-y-1">
              <li>আমরা APK Patch করি না, <strong>NewPipeExtractor</strong> দিয়ে YouTube Scraping করি (Open Source, Legal)</li>
              <li>AdBlock: Service Worker এ Ad Domains Block</li>
              <li>Background: Media Session API + Foreground Service (Android)</li>
              <li>SponsorBlock: Official API (Free)</li>
              <li>Dislike: ReturnYouTubeDislike API (Free)</li>
              <li>এই টেকনিক শিখলে তুমি NewPipe, Seal, YTDLnis এর মতো App বানাতে পারবে!</li>
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
