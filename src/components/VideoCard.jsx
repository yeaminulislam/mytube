import { Link } from 'react-router-dom';

export default function VideoCard({ video }) {
  return (
    <Link to={`/watch/${video.id}`} className="group cursor-pointer">
      <div className="relative rounded-xl overflow-hidden bg-[#181818] aspect-video">
        <img src={video.thumbnail} alt={video.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
        <span className="absolute bottom-2 right-2 bg-black/80 text-white text-xs px-1.5 py-0.5 rounded">
          {video.duration}
        </span>
      </div>
      <div className="flex gap-3 mt-3">
        <img src={video.channelAvatar} alt={video.channel} className="w-9 h-9 rounded-full flex-shrink-0" />
        <div className="flex-1 min-w-0">
          <h3 className="font-medium text-[16px] leading-[22px] line-clamp-2 group-hover:text-[#3ea6ff]">{video.title}</h3>
          <p className="text-[#aaa] text-sm mt-1 flex items-center gap-1">
            {video.channel}
            {video.verified && <span className="bg-[#aaa] text-black rounded-full w-4 h-4 flex items-center justify-center text-[10px]">✓</span>}
          </p>
          <p className="text-[#aaa] text-sm">
            {video.views} ভিউ • {video.timestamp}
          </p>
        </div>
        <button className="opacity-0 group-hover:opacity-100 h-fit p-1">
          <svg className="w-5 h-5" fill="white" viewBox="0 0 24 24"><path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg>
        </button>
      </div>
    </Link>
  );
}
