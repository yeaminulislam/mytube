import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import VideoCard from '../components/VideoCard';
import ShortsCard from '../components/ShortsCard';
import { videos, shorts, categories } from '../data/mockVideos';

export default function Home({ searchQuery }) {
  const [searchParams] = useSearchParams();
  const urlQuery = searchParams.get('q') || '';
  const [selectedCategory, setSelectedCategory] = useState('সব');
  const [filteredVideos, setFilteredVideos] = useState(videos);

  const activeSearch = searchQuery || urlQuery;

  useEffect(() => {
    let result = videos;
    if (activeSearch) {
      result = result.filter(v => 
        v.title.toLowerCase().includes(activeSearch.toLowerCase()) ||
        v.channel.toLowerCase().includes(activeSearch.toLowerCase()) ||
        v.category.toLowerCase().includes(activeSearch.toLowerCase())
      );
    }
    if (selectedCategory !== 'সব') {
      result = result.filter(v => v.category === selectedCategory || selectedCategory === 'আপনার জন্য');
    }
    setFilteredVideos(result);
  }, [activeSearch, selectedCategory]);

  return (
    <div className="flex-1 bg-[#0f0f0f] min-h-[calc(100vh-56px)]">
      {/* Categories */}
      <div className="sticky top-[56px] z-40 bg-[#0f0f0f] px-4 py-3 flex gap-2 overflow-x-auto scrollbar-hide">
        {categories.map(cat => (
          <button
            key={cat}
            onClick={() => setSelectedCategory(cat)}
            className={`px-3 py-1.5 rounded-lg text-sm font-medium whitespace-nowrap transition-colors ${
              selectedCategory === cat ? 'bg-white text-black' : 'bg-[#272727] hover:bg-[#3f3f3f] text-white'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      <div className="p-4 md:p-6">
        {activeSearch && (
          <div className="mb-6">
            <h2 className="text-xl">"{activeSearch}" এর জন্য {filteredVideos.length} টি ফলাফল</h2>
          </div>
        )}

        {/* Shorts Section */}
        {!activeSearch && (
          <div className="mb-8">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold flex items-center gap-3">
                <span className="bg-red-600 p-1 rounded">🎬</span> Shorts
              </h2>
              <button className="p-2 hover:bg-[#272727] rounded-full">✕</button>
            </div>
            <div className="flex gap-4 overflow-x-auto scrollbar-hide pb-2">
              {shorts.map(s => (
                <ShortsCard key={s.id} short={s} />
              ))}
            </div>
            <hr className="mt-6 border-[#303030]" />
          </div>
        )}

        {/* Videos Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-4 gap-y-8">
          {filteredVideos.map(video => (
            <VideoCard key={video.id} video={video} />
          ))}
        </div>

        {filteredVideos.length === 0 && (
          <div className="text-center py-20">
            <p className="text-6xl mb-4">🔍</p>
            <p className="text-xl text-[#aaa]">কোন ভিডিও পাওয়া যায়নি</p>
            <p className="text-[#717171] mt-2">অন্য কিছু সার্চ করে দেখুন</p>
          </div>
        )}

        {/* Android Dev Learning Banner */}
        {!activeSearch && (
          <div className="mt-12 bg-gradient-to-r from-[#1a1a1a] to-[#272727] rounded-2xl p-6 md:p-8 border border-[#303030]">
            <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
              <div>
                <h3 className="text-2xl font-bold mb-2">👨‍💻 আমিন, অ্যান্ড্রয়েড ডেভেলপার হওয়ার যাত্রা শুরু করুন</h3>
                <p className="text-[#aaa] max-w-2xl">MyTube-এ আপনি পাবেন সম্পূর্ণ বাংলায় অ্যান্ড্রয়েড ডেভেলপমেন্ট কোর্স, প্রজেক্ট বেসড লার্নিং, এবং রিয়েল ইউটিউব API ইন্টিগ্রেশন। এই প্রজেক্টটি আপনার পোর্টফোলিওতে যোগ করুন!</p>
                <div className="flex flex-wrap gap-2 mt-4">
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-sm">Java</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-sm">Kotlin</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-sm">Jetpack Compose</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-sm">Firebase</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-sm">Retrofit</span>
                </div>
              </div>
              <div className="bg-white text-black px-6 py-3 rounded-full font-medium hover:bg-[#e5e5e5] cursor-pointer whitespace-nowrap">
                শেখা শুরু করুন →
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
