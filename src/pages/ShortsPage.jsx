import { useState, useEffect, useRef } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { shorts } from '../data/mockVideos';
import { useAuth } from '../context/AuthContext';

export default function ShortsPage() {
  const [searchParams] = useSearchParams();
  const initialId = searchParams.get('id');
  const [activeIndex, setActiveIndex] = useState(() => {
    if (initialId) {
      const idx = shorts.findIndex(s => s.id === initialId);
      return idx >= 0 ? idx : 0;
    }
    return 0;
  });
  const [likedShorts, setLikedShorts] = useState(new Set());
  const [subscribed, setSubscribed] = useState(new Set());
  const containerRef = useRef(null);
  const { user } = useAuth();

  const currentShort = shorts[activeIndex];

  const toggleLike = (id) => {
    setLikedShorts(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const toggleSub = (channel) => {
    setSubscribed(prev => {
      const next = new Set(prev);
      if (next.has(channel)) next.delete(channel);
      else next.add(channel);
      return next;
    });
  };

  useEffect(() => {
    const handleKey = (e) => {
      if (e.key === 'ArrowDown' && activeIndex < shorts.length - 1) {
        setActiveIndex(i => i + 1);
      } else if (e.key === 'ArrowUp' && activeIndex > 0) {
        setActiveIndex(i => i - 1);
      }
    };
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [activeIndex]);

  useEffect(() => {
    if (containerRef.current) {
      const el = containerRef.current.querySelector(`[data-index="${activeIndex}"]`);
      el?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }, [activeIndex]);

  return (
    <div className="flex bg-[#0f0f0f] h-[calc(100vh-56px)] overflow-hidden">
      {/* Shorts Feed */}
      <div ref={containerRef} className="flex-1 overflow-y-auto snap-y snap-mandatory scrollbar-hide">
        {shorts.map((short, idx) => (
          <div key={short.id} data-index={idx} className="snap-start h-[calc(100vh-56px)] flex justify-center items-center py-4 relative">
            <div className="flex gap-4 h-full max-h-[calc(100vh-80px)]">
              {/* Video */}
              <div className="relative w-[360px] max-w-[90vw] h-full bg-black rounded-xl overflow-hidden group">
                <iframe
                  width="100%"
                  height="100%"
                  src={`https://www.youtube.com/embed/${short.videoId}?controls=0&loop=1&modestbranding=1&rel=0&playlist=${short.videoId}`}
                  title={short.title}
                  frameBorder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope"
                  className="w-full h-full"
                ></iframe>

                {/* Overlay gradient */}
                <div className="absolute inset-0 pointer-events-none bg-gradient-to-t from-black/40 via-transparent to-transparent" />

                {/* Bottom info */}
                <div className="absolute bottom-0 left-0 right-0 p-4 text-white pointer-events-none">
                  <p className="font-medium text-[15px] leading-5 line-clamp-2">{short.title}</p>
                  <div className="flex items-center gap-2 mt-3 pointer-events-auto">
                    <span className="text-sm">@{short.channel}</span>
                    <button
                      onClick={() => toggleSub(short.channel)}
                      className={`px-3 py-1 rounded-full text-sm font-medium ${subscribed.has(short.channel) ? 'bg-[#272727] text-[#aaa]' : 'bg-white text-black'}`}
                    >
                      {subscribed.has(short.channel) ? 'সাবস্ক্রাইবড' : 'সাবস্ক্রাইব'}
                    </button>
                  </div>
                </div>

                {/* Progress */}
                <div className="absolute top-0 left-0 right-0 h-1 bg-white/20">
                  <div className="h-full bg-red-600 w-[35%] animate-pulse" />
                </div>
              </div>

              {/* Actions */}
              <div className="flex flex-col justify-end gap-6 pb-4">
                <button onClick={() => toggleLike(short.id)} className="flex flex-col items-center gap-1 group">
                  <div className={`w-12 h-12 rounded-full flex items-center justify-center ${likedShorts.has(short.id) ? 'bg-red-600' : 'bg-[#272727] group-hover:bg-[#3f3f3f]'}`}>
                    <span className="text-xl">{likedShorts.has(short.id) ? '❤️' : '👍'}</span>
                  </div>
                  <span className="text-xs">{likedShorts.has(short.id) ? `${(parseInt(short.likes) + 1).toLocaleString()}` : short.likes}</span>
                </button>

                <button className="flex flex-col items-center gap-1 group">
                  <div className="w-12 h-12 bg-[#272727] rounded-full flex items-center justify-center group-hover:bg-[#3f3f3f]">
                    <span className="text-xl">👎</span>
                  </div>
                  <span className="text-xs">ডিসলাইক</span>
                </button>

                <button className="flex flex-col items-center gap-1 group">
                  <div className="w-12 h-12 bg-[#272727] rounded-full flex items-center justify-center group-hover:bg-[#3f3f3f]">
                    <span className="text-xl">💬</span>
                  </div>
                  <span className="text-xs">1.2K</span>
                </button>

                <button className="flex flex-col items-center gap-1 group">
                  <div className="w-12 h-12 bg-[#272727] rounded-full flex items-center justify-center group-hover:bg-[#3f3f3f]">
                    <span className="text-xl">↗️</span>
                  </div>
                  <span className="text-xs">শেয়ার</span>
                </button>

                <button className="flex flex-col items-center gap-1">
                  <div className="w-12 h-12 rounded-full overflow-hidden border-2 border-[#3f3f3f]">
                    <img src={`https://i.pravatar.cc/100?img=${idx + 1}`} className="w-full h-full" alt="" />
                  </div>
                </button>
              </div>
            </div>

            {/* Navigation arrows - desktop */}
            <div className="hidden lg:flex flex-col gap-2 absolute right-10 top-1/2 -translate-y-1/2">
              <button
                onClick={() => setActiveIndex(i => Math.max(0, i - 1))}
                disabled={activeIndex === 0}
                className="w-10 h-10 bg-[#272727] rounded-full flex items-center justify-center hover:bg-[#3f3f3f] disabled:opacity-30"
              >
                ▲
              </button>
              <button
                onClick={() => setActiveIndex(i => Math.min(shorts.length - 1, i + 1))}
                disabled={activeIndex === shorts.length - 1}
                className="w-10 h-10 bg-[#272727] rounded-full flex items-center justify-center hover:bg-[#3f3f3f] disabled:opacity-30"
              >
                ▼
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Right sidebar - comments (only on large screen and when active) */}
      <div className="hidden xl:block w-[400px] border-l border-[#303030] p-4 overflow-y-auto">
        <h3 className="font-bold text-lg mb-4">Shorts সম্পর্কে</h3>
        <div className="bg-[#272727] rounded-xl p-4">
          <h4 className="font-medium">{currentShort?.title}</h4>
          <p className="text-sm text-[#aaa] mt-2">{currentShort?.views} ভিউ • 2 দিন আগে</p>
          <div className="flex gap-2 mt-4">
            <span className="bg-[#3f3f3f] px-2 py-1 rounded-full text-xs">#shorts</span>
            <span className="bg-[#3f3f3f] px-2 py-1 rounded-full text-xs">#android</span>
            <span className="bg-[#3f3f3f] px-2 py-1 rounded-full text-xs">#coding</span>
          </div>
        </div>

        <div className="mt-6">
          <h4 className="font-medium mb-3">মন্তব্য {user ? '' : '(লগইন করুন)'}</h4>
          {user ? (
            <div className="space-y-4">
              <div className="flex gap-2">
                <img src={user.avatar} className="w-8 h-8 rounded-full" alt="" />
                <input placeholder="মন্তব্য যোগ করুন..." className="flex-1 bg-[#272727] rounded-full px-4 py-2 text-sm focus:outline-none" />
              </div>
              <div className="flex gap-3">
                <img src="https://i.pravatar.cc/100?img=12" className="w-8 h-8 rounded-full" alt="" />
                <div>
                  <p className="text-sm"><span className="font-medium">@rahim_dev</span> <span className="text-[#aaa] text-xs">1 ঘন্টা আগে</span></p>
                  <p className="text-sm mt-1">ভাই শর্টসটা দারুণ হয়েছে! 🔥</p>
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-[#272727] p-4 rounded-xl text-center">
              <p className="text-sm text-[#aaa]">মন্তব্য দেখতে ও করতে</p>
              <Link to="/login" className="mt-2 inline-block bg-white text-black px-4 py-1.5 rounded-full text-sm font-medium">লগইন করুন</Link>
            </div>
          )}
        </div>

        <div className="mt-8 p-4 bg-gradient-to-br from-red-900/20 to-[#272727] rounded-xl border border-red-900/30">
          <p className="text-sm font-medium">🎓 আমিনের টিপস</p>
          <p className="text-xs text-[#aaa] mt-2">YouTube Shorts হলো অ্যান্ড্রয়েড অ্যাপে Vertical ViewPager2 + ExoPlayer দিয়ে বানানো হয়। এই MyTube প্রজেক্টে আমরা একই UX ওয়েবে তৈরি করেছি!</p>
        </div>
      </div>
    </div>
  );
}
