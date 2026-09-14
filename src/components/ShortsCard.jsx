import { Link } from 'react-router-dom';

export default function ShortsCard({ short }) {
  return (
    <Link to={`/shorts?id=${short.id}`} className="group cursor-pointer block">
      <div className="relative rounded-xl overflow-hidden bg-[#181818] aspect-[9/16] max-w-[180px]">
        <img src={short.thumbnail} alt={short.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
        <div className="absolute bottom-2 left-2 right-2">
          <p className="text-white text-[11px]">{short.views} ভিউ</p>
        </div>
      </div>
      <div className="mt-2 max-w-[180px]">
        <h3 className="text-sm leading-5 line-clamp-2">{short.title}</h3>
      </div>
    </Link>
  );
}
