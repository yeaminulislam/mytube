import { useParams, Link } from 'react-router-dom';
import { videos } from '../data/mockVideos';
import VideoCard from '../components/VideoCard';
import { useAuth } from '../context/AuthContext';
import { useState } from 'react';

export default function VideoPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [liked, setLiked] = useState(false);
  const [subscribed, setSubscribed] = useState(false);
  const [comment, setComment] = useState('');
  const [comments, setComments] = useState([
    { id: 1, user: 'রহিম', avatar: 'https://i.pravatar.cc/100?img=10', text: 'অসাধারণ টিউটোরিয়াল! অনেক কিছু শিখলাম।', time: '2 ঘন্টা আগে', likes: 24 },
    { id: 2, user: 'করিম', avatar: 'https://i.pravatar.cc/100?img=11', text: 'আমিন ভাই, পরের ভিডিওতে Firebase নিয়ে বিস্তারিত দেখাবেন প্লিজ।', time: '5 ঘন্টা আগে', likes: 12 },
  ]);

  const video = videos.find(v => v.id === id) || videos[0];
  const related = videos.filter(v => v.id !== video.id).slice(0, 8);

  const handleComment = (e) => {
    e.preventDefault();
    if (!comment.trim() || !user) return;
    setComments([{ id: Date.now(), user: user.name, avatar: user.avatar, text: comment, time: 'এখনই', likes: 0 }, ...comments]);
    setComment('');
  };

  return (
    <div className="flex flex-col lg:flex-row gap-6 p-4 lg:p-6 bg-[#0f0f0f] min-h-[calc(100vh-56px)]">
      {/* Main */}
      <div className="flex-1 max-w-[1280px]">
        {/* Player */}
        <div className="aspect-video bg-black rounded-xl overflow-hidden">
          <iframe
            width="100%"
            height="100%"
            src={`https://www.youtube.com/embed/${video.id}?autoplay=1`}
            title={video.title}
            frameBorder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          ></iframe>
        </div>

        <div className="mt-4">
          <h1 className="text-xl font-bold leading-7">{video.title}</h1>
          
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mt-3">
            <div className="flex items-center gap-3">
              <img src={video.channelAvatar} className="w-10 h-10 rounded-full" alt="" />
              <div>
                <p className="font-medium flex items-center gap-1">{video.channel} {video.verified && <span className="bg-[#aaa] text-black rounded-full w-4 h-4 flex items-center justify-center text-[10px]">✓</span>}</p>
                <p className="text-sm text-[#aaa]">1.2M সাবস্ক্রাইবার</p>
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
                  <span>{liked ? '👍' : '👍'}</span> <span className="text-sm font-medium">{liked ? '12K' : '11K'}</span>
                </button>
                <div className="w-[1px] bg-[#3f3f3f] my-2" />
                <button className="px-3 py-2 hover:bg-[#3f3f3f]">👎</button>
              </div>
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
              <span className="text-[#3ea6ff]">#{video.category}</span>
            </div>
            <p className="text-sm mt-2 line-clamp-2">{video.description}</p>
            <button className="text-sm font-medium mt-2">আরও দেখুন...</button>
          </div>

          {/* Comments */}
          <div className="mt-6">
            <h3 className="text-xl font-bold flex items-center gap-6">
              {comments.length} টি মন্তব্য
              <span className="text-sm font-normal flex items-center gap-2">↕️ সাজান</span>
            </h3>

            {user ? (
              <form onSubmit={handleComment} className="flex gap-3 mt-6">
                <img src={user.avatar} className="w-10 h-10 rounded-full" alt="" />
                <div className="flex-1">
                  <input
                    value={comment}
                    onChange={e => setComment(e.target.value)}
                    placeholder="মন্তব্য যোগ করুন..."
                    className="w-full bg-transparent border-b border-[#303030] pb-1 focus:outline-none focus:border-white text-sm"
                  />
                  <div className="flex justify-end gap-2 mt-3">
                    <button type="button" onClick={() => setComment('')} className="px-4 py-2 rounded-full text-sm hover:bg-[#272727]">বাতিল</button>
                    <button disabled={!comment.trim()} className="px-4 py-2 rounded-full text-sm bg-[#3ea6ff] text-black disabled:bg-[#272727] disabled:text-[#717171] font-medium">মন্তব্য</button>
                  </div>
                </div>
              </form>
            ) : (
              <div className="mt-6 p-4 bg-[#272727] rounded-xl text-center">
                <p className="text-sm">মন্তব্য করতে <Link to="/login" className="text-[#3ea6ff]">লগইন করুন</Link></p>
              </div>
            )}

            <div className="mt-6 space-y-5">
              {comments.map(c => (
                <div key={c.id} className="flex gap-3">
                  <img src={c.avatar} className="w-10 h-10 rounded-full" alt="" />
                  <div className="flex-1">
                    <p className="text-sm"><span className="font-medium">@{c.user}</span> <span className="text-[#aaa] ml-2">{c.time}</span></p>
                    <p className="text-sm mt-1">{c.text}</p>
                    <div className="flex items-center gap-4 mt-2">
                      <button className="flex items-center gap-1 text-sm text-[#aaa] hover:text-white">👍 {c.likes}</button>
                      <button className="text-sm text-[#aaa]">👎</button>
                      <button className="text-xs bg-[#272727] px-3 py-1 rounded-full">উত্তর দিন</button>
                    </div>
                  </div>
                </div>
              ))}
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
