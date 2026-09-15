import { useParams, Link } from 'react-router-dom';
import { useState, useEffect, useRef } from 'react';
import VideoCard from '../components/VideoCard';
import VancedPanel from '../components/VancedPanel';
import { useAuth } from '../context/AuthContext';
import { useVancedFeatures } from '../hooks/useVancedFeatures';
import { getVideoDetails, getRelatedVideos, getVideoComments, getChannelDetails, formatViewCount } from '../services/youtubeApi';

export default function VideoPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [video, setVideo] = useState(null);
  const [related, setRelated] = useState([]);
  const [comments, setComments] = useState([]);
  const [channel, setChannel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [liked, setLiked] = useState(false);
  const [subscribed, setSubscribed] = useState(false);
  const [commentText, setCommentText] = useState('');
  const [localComments, setLocalComments] = useState([]);
  const [showDescription, setShowDescription] = useState(false);
  const videoRef = useRef(null);

  const vanced = useVancedFeatures(id, videoRef);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Parallel fetch - Real YouTube API calls
        const [videoData, relatedData, commentsData] = await Promise.all([
          getVideoDetails(id),
          getRelatedVideos(id, 12),
          getVideoComments(id, 20)
        ]);

        console.log('📺 Video details:', videoData);
        console.log('📺 Related:', relatedData);
        console.log('💬 Comments:', commentsData);

        setVideo(videoData);
        setRelated(relatedData);
        setComments(commentsData);

        // Channel details if available
        if (videoData.channelId) {
          const channelData = await getChannelDetails(videoData.channelId);
          setChannel(channelData);
        }
      } catch (err) {
        console.error('Video page fetch error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    window.scrollTo(0, 0);
  }, [id]);

  const handleComment = (e) => {
    e.preventDefault();
    if (!commentText.trim() || !user) return;
    
    const newComment = {
      id: Date.now(),
      author: user.name,
      avatar: user.avatar,
      text: commentText,
      time: 'এখনই',
      likes: 0,
      isLocal: true
    };
    
    setLocalComments([newComment, ...localComments]);
    setCommentText('');
  };

  if (loading) {
    return (
      <div className="flex flex-col lg:flex-row gap-6 p-4 lg:p-6 bg-[#0f0f0f] min-h-[calc(100vh-56px)] animate-pulse">
        <div className="flex-1">
          <div className="aspect-video bg-[#272727] rounded-xl"></div>
          <div className="h-6 bg-[#272727] rounded mt-4 w-3/4"></div>
          <div className="h-4 bg-[#272727] rounded mt-2 w-1/2"></div>
        </div>
        <div className="w-full lg:w-[402px] space-y-3">
          {[1,2,3,4].map(i => <div key={i} className="h-24 bg-[#272727] rounded-xl"></div>)}
        </div>
      </div>
    );
  }

  if (!video) return <div className="p-10 text-center">ভিডিও পাওয়া যায়নি</div>;

  const allComments = [...localComments, ...comments];

  return (
    <div className="flex flex-col lg:flex-row gap-6 p-4 lg:p-6 bg-[#0f0f0f] min-h-[calc(100vh-56px)]">
      {/* Main */}
      <div className="flex-1 max-w-[1280px]">
        {/* Player - With Vanced Features */}
        <div className="aspect-video bg-black rounded-xl overflow-hidden relative">
          <iframe
            ref={videoRef}
            width="100%"
            height="100%"
            src={`https://www.youtube.com/embed/${video.id}?autoplay=1&rel=0&modestbranding=1&enablejsapi=1`}
            title={video.title}
            frameBorder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          ></iframe>
          {/* Vanced AdBlock Overlay - Simulated */}
          {vanced.isAdBlockEnabled && (
            <div className="absolute top-2 left-2 bg-red-600 text-white text-xs px-2 py-1 rounded-full font-bold animate-pulse">
              🚫 AdBlock ON
            </div>
          )}
        </div>

        {/* Vanced Panel - Like Vanced Settings */}
        <VancedPanel vanced={vanced} />

        <div className="mt-4">
          <h1 className="text-xl font-bold leading-7">{video.title}</h1>
          
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mt-3">
            <div className="flex items-center gap-3">
              <img src={channel?.avatar || video.channelAvatar} className="w-10 h-10 rounded-full" alt="" />
              <div>
                <p className="font-medium flex items-center gap-1">
                  {video.channel}
                  {video.verified && <span className="bg-[#aaa] text-black rounded-full w-4 h-4 flex items-center justify-center text-[10px]">✓</span>}
                </p>
                <p className="text-sm text-[#aaa]">
                  {channel?.subscribers || '1.2M'} সাবস্ক্রাইবার
                  {video.isMock && <span className="ml-2 bg-yellow-900/30 text-yellow-400 px-1.5 py-0.5 rounded text-xs">Mock</span>}
                  {!video.isMock && <span className="ml-2 bg-green-900/30 text-green-400 px-1.5 py-0.5 rounded text-xs">Real API</span>}
                </p>
              </div>
              <button
                onClick={() => setSubscribed(!subscribed)}
                className={`ml-4 px-4 py-2 rounded-full font-medium text-sm ${subscribed ? 'bg-[#272727] text-[#aaa]' : 'bg-white text-black hover:bg-[#e5e5e5]'}`}
              >
                {subscribed ? 'সাবস্ক্রাইবড ✓' : 'সাবস্ক্রাইব'}
              </button>
            </div>

            <div className="flex items-center gap-2">
              <div className="flex bg-[#272727] rounded-full overflow-hidden">
                <button onClick={() => setLiked(!liked)} className="flex items-center gap-2 px-4 py-2 hover:bg-[#3f3f3f]">
                  <span>{liked ? '👍' : '👍'}</span> 
                  <span className="text-sm font-medium">{liked ? formatViewCount((parseInt(video.rawLikeCount || 11000) + 1).toString()) : (video.likeCount || '11K')}</span>
                </button>
                <div className="w-[1px] bg-[#3f3f3f] my-2" />
                <button className="px-3 py-2 hover:bg-[#3f3f3f] flex items-center gap-2">
                  <span>👎</span>
                  {vanced.dislikeData && <span className="text-xs">{vanced.dislikeData.dislikes > 1000 ? `${(vanced.dislikeData.dislikes/1000).toFixed(1)}K` : vanced.dislikeData.dislikes}</span>}
                </button>
              </div>
              <button onClick={vanced.enablePiP} className="bg-[#272727] px-4 py-2 rounded-full text-sm font-medium hover:bg-[#3f3f3f] flex items-center gap-2">
                <span>📺</span> PiP
              </button>
              <button className="bg-[#272727] px-4 py-2 rounded-full text-sm font-medium hover:bg-[#3f3f3f] flex items-center gap-2">
                <span>↗️</span> শেয়ার
              </button>
              <button className="bg-[#272727] p-2 rounded-full hover:bg-[#3f3f3f]">⋯</button>
            </div>
          </div>

          <div className="mt-4 bg-[#272727] rounded-xl p-3">
            <div className="flex gap-4 text-sm font-medium">
              <span>{video.views} ভিউ</span>
              <span>{video.timestamp}</span>
              {video.tags?.slice(0, 3).map(tag => (
                <span key={tag} className="text-[#3ea6ff]">#{tag.replace(/\s+/g, '')}</span>
              ))}
            </div>
            <div className={`text-sm mt-2 ${!showDescription ? 'line-clamp-2' : ''} whitespace-pre-wrap`}>
              {video.description || 'No description'}
            </div>
            <button onClick={() => setShowDescription(!showDescription)} className="text-sm font-medium mt-2 hover:text-[#3ea6ff]">
              {showDescription ? 'কম দেখান' : 'আরও দেখুন...'}
            </button>

            {/* Debug: How API works */}
            <details className="mt-3 bg-[#0f0f0f] p-2 rounded text-xs">
              <summary className="cursor-pointer text-[#717171]">🔍 কিভাবে এই ডাটা আসলো? (Developer View)</summary>
              <pre className="mt-2 text-[11px] text-[#aaa] overflow-x-auto">
{`// youtubeApi.js - getVideoDetails()
const data = await fetch(
  \`https://www.googleapis.com/youtube/v3/videos?
  part=contentDetails,statistics,snippet
  &id=\${videoId}
  &key=\${API_KEY}\`
)
→ views: \${video.rawViews || video.views}
→ likes: \${video.rawLikeCount || 'N/A'}
→ channelId: \${video.channelId || 'mock'}`}
              </pre>
            </details>
          </div>

          {/* Comments */}
          <div className="mt-6">
            <h3 className="text-xl font-bold flex items-center gap-6">
              {allComments.length} টি মন্তব্য
              <span className="text-sm font-normal flex items-center gap-2 text-[#aaa]">↕️ {video.isMock ? 'Mock' : 'Real YouTube Comments'}</span>
            </h3>

            {user ? (
              <form onSubmit={handleComment} className="flex gap-3 mt-6">
                <img src={user.avatar} className="w-10 h-10 rounded-full" alt="" />
                <div className="flex-1">
                  <input
                    value={commentText}
                    onChange={e => setCommentText(e.target.value)}
                    placeholder="মন্তব্য যোগ করুন..."
                    className="w-full bg-transparent border-b border-[#303030] pb-1 focus:outline-none focus:border-white text-sm"
                  />
                  <div className="flex justify-end gap-2 mt-3">
                    <button type="button" onClick={() => setCommentText('')} className="px-4 py-2 rounded-full text-sm hover:bg-[#272727]">বাতিল</button>
                    <button disabled={!commentText.trim()} className="px-4 py-2 rounded-full text-sm bg-[#3ea6ff] text-black disabled:bg-[#272727] disabled:text-[#717171] font-medium">মন্তব্য</button>
                  </div>
                </div>
              </form>
            ) : (
              <div className="mt-6 p-4 bg-[#272727] rounded-xl text-center">
                <p className="text-sm">মন্তব্য করতে <Link to="/login" className="text-[#3ea6ff]">লগইন করুন</Link></p>
                <p className="text-xs text-[#717171] mt-1">Real YouTube API তে কমেন্ট পোস্ট করতে OAuth scope লাগে</p>
              </div>
            )}

            <div className="mt-6 space-y-5">
              {allComments.map(c => (
                <div key={c.id} className="flex gap-3">
                  <img src={c.avatar} className="w-10 h-10 rounded-full" alt="" />
                  <div className="flex-1">
                    <p className="text-sm"><span className="font-medium">@{c.author}</span> <span className="text-[#aaa] ml-2">{c.time}</span> {c.isLocal && <span className="bg-[#3ea6ff]/20 text-[#3ea6ff] px-1.5 py-0.5 rounded text-xs ml-2">Local</span>}</p>
                    <p className="text-sm mt-1 whitespace-pre-wrap">{c.text}</p>
                    <div className="flex items-center gap-4 mt-2">
                      <button className="flex items-center gap-1 text-sm text-[#aaa] hover:text-white">👍 {c.likes || 0}</button>
                      <button className="text-sm text-[#aaa]">👎</button>
                      <button className="text-xs bg-[#272727] px-3 py-1 rounded-full">উত্তর দিন</button>
                    </div>
                  </div>
                </div>
              ))}
              {allComments.length === 0 && (
                <p className="text-sm text-[#717171] text-center py-8">কোন মন্তব্য নেই। প্রথম মন্তব্য করুন!</p>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Sidebar - Related */}
      <div className="w-full lg:w-[402px] flex-shrink-0 space-y-2">
        <div className="flex gap-2 overflow-x-auto scrollbar-hide pb-2">
          <button className="bg-white text-black px-3 py-1.5 rounded-lg text-sm font-medium whitespace-nowrap">সব</button>
          <button className="bg-[#272727] px-3 py-1.5 rounded-lg text-sm whitespace-nowrap">আপনার জন্য</button>
          <button className="bg-[#272727] px-3 py-1.5 rounded-lg text-sm whitespace-nowrap">অ্যান্ড্রয়েড</button>
          <button className="bg-[#272727] px-3 py-1.5 rounded-lg text-sm whitespace-nowrap">সম্পর্কিত</button>
        </div>

        {channel && (
          <div className="bg-[#272727] rounded-xl p-3 mb-4">
            <div className="flex gap-3">
              <img src={channel.avatar} className="w-12 h-12 rounded-full" alt="" />
              <div>
                <p className="font-medium">{channel.title}</p>
                <p className="text-xs text-[#aaa]">{channel.subscribers} • {channel.videoCount} videos</p>
              </div>
            </div>
          </div>
        )}

        {related.map(v => (
          <div key={v.id} className="flex gap-2 group">
            <Link to={`/watch/${v.id}`} className="w-[168px] aspect-video rounded-lg overflow-hidden bg-[#181818] flex-shrink-0 relative">
              <img src={v.thumbnail} className="w-full h-full object-cover" alt="" />
              <span className="absolute bottom-1 right-1 bg-black/80 text-[11px] px-1 rounded">{v.duration}</span>
            </Link>
            <div className="flex-1 min-w-0 py-1">
              <Link to={`/watch/${v.id}`} className="text-sm font-medium leading-5 line-clamp-2 group-hover:text-[#3ea6ff]">{v.title}</Link>
              <p className="text-xs text-[#aaa] mt-1">{v.channel}</p>
              <p className="text-xs text-[#aaa]">{v.views} • {v.timestamp}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
