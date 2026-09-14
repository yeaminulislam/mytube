import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import VideoCard from '../components/VideoCard';
import ShortsCard from '../components/ShortsCard';
import ApiSetupBanner from '../components/ApiSetupBanner';
import { categories } from '../data/mockVideos';
import { searchVideos, getPopularVideos, getShorts, apiStatus } from '../services/youtubeApi';

export default function Home({ searchQuery }) {
  const [searchParams] = useSearchParams();
  const urlQuery = searchParams.get('q') || '';
  const [selectedCategory, setSelectedCategory] = useState('সব');
  const [videos, setVideos] = useState([]);
  const [shorts, setShorts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [nextPageToken, setNextPageToken] = useState('');
  const [error, setError] = useState(null);

  const activeSearch = searchQuery || urlQuery;

  // Fetch videos - Real API or Mock
  const fetchVideos = async (isNewSearch = true) => {
    try {
      if (isNewSearch) setLoading(true);
      else setLoadingMore(true);
      
      setError(null);

      let result;
      const pageToken = isNewSearch ? '' : nextPageToken;

      if (activeSearch) {
        // Real YouTube Search
        result = await searchVideos(activeSearch, pageToken);
      } else if (selectedCategory !== 'সব') {
        // Category search
        const query = selectedCategory === 'আপনার জন্য' ? 'android development bangla' : selectedCategory;
        result = await searchVideos(query, pageToken);
      } else {
        // Popular videos
        result = await getPopularVideos('', pageToken);
      }

      console.log('📺 Fetched videos:', result);

      if (isNewSearch) {
        setVideos(result.videos);
      } else {
        setVideos(prev => [...prev, ...result.videos]);
      }
      setNextPageToken(result.nextPageToken || '');

      if (result.error) {
        setError(result.error);
      }

    } catch (err) {
      console.error('Fetch videos error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  const fetchShorts = async () => {
    try {
      const shortsData = await getShorts('android development shorts bangla');
      setShorts(shortsData);
    } catch (err) {
      console.error('Fetch shorts error:', err);
    }
  };

  useEffect(() => {
    fetchVideos(true);
  }, [activeSearch, selectedCategory]);

  useEffect(() => {
    fetchShorts();
  }, []);

  const handleLoadMore = () => {
    if (nextPageToken) {
      fetchVideos(false);
    }
  };

  return (
    <div className="flex-1 bg-[#0f0f0f] min-h-[calc(100vh-56px)]">
      {/* API Status Banner */}
      <ApiSetupBanner />

      {/* Categories */}
      <div className="sticky top-[56px] z-40 bg-[#0f0f0f] px-4 py-3 flex gap-2 overflow-x-auto scrollbar-hide border-b border-[#212121]">
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
        <div className="ml-auto flex items-center gap-2 text-xs text-[#717171] pl-4 border-l border-[#303030]">
          <span className={`w-2 h-2 rounded-full ${apiStatus.isMock ? 'bg-yellow-500' : 'bg-green-500'}`}></span>
          {apiStatus.isMock ? 'Demo Mode' : 'Live API'}
        </div>
      </div>

      <div className="p-4 md:p-6">
        {activeSearch && !loading && (
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-xl">"{activeSearch}" এর জন্য {videos.length} টি ফলাফল</h2>
            {!apiStatus.isMock && <span className="text-xs bg-green-900/30 text-green-400 px-2 py-1 rounded-full border border-green-800">Real YouTube API</span>}
          </div>
        )}

        {error && (
          <div className="mb-6 bg-red-900/20 border border-red-800/50 rounded-xl p-4">
            <p className="text-red-400 text-sm">⚠️ API Error: {error}</p>
            <p className="text-[#aaa] text-xs mt-1">Demo data দেখানো হচ্ছে। .env এ সঠিক API Key দিন।</p>
          </div>
        )}

        {/* Shorts Section */}
        {!activeSearch && shorts.length > 0 && (
          <div className="mb-8">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold flex items-center gap-3">
                <span className="bg-red-600 p-1 rounded">🎬</span> Shorts
                {!apiStatus.isMock && <span className="text-xs font-normal bg-[#272727] px-2 py-1 rounded-full">{shorts.length} real shorts</span>}
              </h2>
            </div>
            <div className="flex gap-4 overflow-x-auto scrollbar-hide pb-2">
              {shorts.map(s => (
                <ShortsCard key={s.id || s.videoId} short={s} />
              ))}
            </div>
            <hr className="mt-6 border-[#303030]" />
          </div>
        )}

        {/* Loading */}
        {loading ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-4 gap-y-8">
            {[1,2,3,4,5,6,7,8].map(i => (
              <div key={i} className="animate-pulse">
                <div className="bg-[#272727] aspect-video rounded-xl"></div>
                <div className="flex gap-3 mt-3">
                  <div className="w-9 h-9 bg-[#272727] rounded-full"></div>
                  <div className="flex-1 space-y-2">
                    <div className="h-4 bg-[#272727] rounded w-full"></div>
                    <div className="h-3 bg-[#272727] rounded w-3/4"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <>
            {/* Videos Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-x-4 gap-y-8">
              {videos.map(video => (
                <VideoCard key={video.id} video={video} />
              ))}
            </div>

            {videos.length === 0 && (
              <div className="text-center py-20">
                <p className="text-6xl mb-4">🔍</p>
                <p className="text-xl text-[#aaa]">কোন ভিডিও পাওয়া যায়নি</p>
                <p className="text-[#717171] mt-2">অন্য কিছু সার্চ করে দেখুন</p>
              </div>
            )}

            {/* Load More */}
            {nextPageToken && (
              <div className="text-center mt-8">
                <button
                  onClick={handleLoadMore}
                  disabled={loadingMore}
                  className="bg-[#272727] hover:bg-[#3f3f3f] px-6 py-2 rounded-full text-sm font-medium disabled:opacity-50"
                >
                  {loadingMore ? 'লোড হচ্ছে...' : 'আরও দেখুন ↓'}
                </button>
              </div>
            )}
          </>
        )}

        {/* Android Dev Learning Banner */}
        {!activeSearch && !loading && (
          <div className="mt-12 bg-gradient-to-r from-[#1a1a1a] to-[#272727] rounded-2xl p-6 md:p-8 border border-[#303030]">
            <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
              <div>
                <h3 className="text-2xl font-bold mb-2">👨‍💻 কিভাবে এই কোড কাজ করে?</h3>
                <p className="text-[#aaa] max-w-3xl text-sm leading-6">
                  <strong className="text-white">youtubeApi.js:</strong> গুগলের YouTube Data API v3 এর সাথে fetch() দিয়ে কথা বলে। 
                  <code className="bg-[#0f0f0f] px-1.5 py-0.5 rounded mx-1">searchVideos()</code> ফাংশন <code className="bg-[#0f0f0f] px-1 py-0.5 rounded">/search</code> endpoint কল করে, তারপর <code className="bg-[#0f0f0f] px-1 py-0.5 rounded">/videos</code> দিয়ে views, duration আনে।<br/>
                  <strong className="text-white mt-2 block">googleAuth.js:</strong> Google Identity Services (GSI) লোড করে, JWT decode করে ইউজার বের করে। OAuth 2.0 দিয়ে YouTube এর private data access নেয়।
                </p>
                <div className="flex flex-wrap gap-2 mt-4">
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-xs">YouTube Data API v3</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-xs">Google Identity Services</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-xs">OAuth 2.0</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-xs">JWT</span>
                  <span className="bg-[#3f3f3f] px-3 py-1 rounded-full text-xs">REST API</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
