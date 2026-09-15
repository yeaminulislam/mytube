import { useState, useEffect } from 'react';
import { usePWAInstall } from '../hooks/usePWAInstall';

export default function InstallPrompt() {
  const { isInstallable, isInstalled, installApp } = usePWAInstall();
  const [dismissed, setDismissed] = useState(false);
  const [showIOSHint, setShowIOSHint] = useState(false);

  useEffect(() => {
    // Detect iOS
    const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent);
    const isInStandalone = window.navigator.standalone;
    
    if (isIOS && !isInStandalone) {
      setShowIOSHint(true);
    }

    // Check if dismissed recently
    const dismissedTime = localStorage.getItem('mytube_install_dismissed');
    if (dismissedTime) {
      const hoursSince = (Date.now() - parseInt(dismissedTime)) / (1000 * 60 * 60);
      if (hoursSince < 24) setDismissed(true); // Don't show again for 24h
    }
  }, []);

  const handleDismiss = () => {
    setDismissed(true);
    localStorage.setItem('mytube_install_dismissed', Date.now().toString());
  };

  if (isInstalled) {
    return (
      <div className="bg-green-900/20 border border-green-800/50 rounded-xl m-4 p-3 flex items-center gap-3">
        <span className="text-2xl">✅</span>
        <div className="flex-1">
          <p className="font-bold text-sm text-green-400">MyTube ইন্সটল করা আছে!</p>
          <p className="text-xs text-green-300/70">হোম স্ক্রিন থেকে ওপেন করুন - YouTube এর মতো কাজ করবে</p>
        </div>
      </div>
    );
  }

  if (dismissed) return null;

  // Android Chrome Install Prompt
  if (isInstallable) {
    return (
      <div className="bg-gradient-to-r from-red-600 to-red-800 rounded-xl m-4 p-4 flex items-start gap-4 shadow-2xl animate-pulse">
        <div className="bg-white rounded-xl p-2">
          <span className="text-2xl">📱</span>
        </div>
        <div className="flex-1">
          <h3 className="font-bold text-white">MyTube ফোনে ইন্সটল করুন!</h3>
          <p className="text-sm text-red-100 mt-1">YouTube এর মতো App হিসেবে ব্যবহার করুন - Offline ও কাজ করবে</p>
          <div className="flex gap-2 mt-3">
            <button
              onClick={installApp}
              className="bg-white text-red-600 px-5 py-2 rounded-full font-bold text-sm hover:bg-red-50"
            >
              📲 ইন্সটল করুন
            </button>
            <button
              onClick={handleDismiss}
              className="bg-black/20 text-white px-4 py-2 rounded-full text-sm hover:bg-black/30"
            >
              পরে
            </button>
          </div>
          <p className="text-xs text-red-200 mt-2">💡 এক ক্লিকে ইন্সটল - কোনো APK লাগবে না!</p>
        </div>
        <button onClick={handleDismiss} className="text-white/70 hover:text-white">✕</button>
      </div>
    );
  }

  // iOS Hint
  if (showIOSHint) {
    return (
      <div className="bg-[#212121] border border-[#303030] rounded-xl m-4 p-4">
        <div className="flex gap-3">
          <span className="text-2xl">📱</span>
          <div className="flex-1">
            <h3 className="font-bold text-sm">iPhone এ ইন্সটল করুন</h3>
            <ol className="text-xs text-[#aaa] mt-2 space-y-1 list-decimal list-inside">
              <li>Safari তে Share বাটন (⎙) চাপুন</li>
              <li>"Add to Home Screen" → "Add"</li>
              <li>হোম স্ক্রিনে MyTube আইকন আসবে!</li>
            </ol>
            <button onClick={handleDismiss} className="mt-3 bg-[#303030] px-4 py-1.5 rounded-full text-xs">বুঝেছি</button>
          </div>
        </div>
      </div>
    );
  }

  return null;
}
