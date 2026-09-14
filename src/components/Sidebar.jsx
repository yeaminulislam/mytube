import { Link, useLocation } from 'react-router-dom';

export default function Sidebar({ collapsed }) {
  const location = useLocation();
  const isActive = (path) => location.pathname === path;

  const menuItems = [
    { icon: "🏠", label: "হোম", path: "/", active: true },
    { icon: "🎬", label: "Shorts", path: "/shorts" },
    { icon: "📺", label: "সাবস্ক্রিপশন", path: "/subscriptions" },
    { divider: true },
    { icon: "📚", label: "আপনি", path: "/you", section: "আপনি >" },
    { icon: "🕒", label: "ইতিহাস", path: "/history" },
    { icon: "🎵", label: "প্লেলিস্ট", path: "/playlists" },
    { icon: "▶️", label: "আপনার ভিডিও", path: "/your-videos" },
    { icon: "⏰", label: "পরে দেখুন", path: "/watch-later" },
    { icon: "👍", label: "পছন্দ করা ভিডিও", path: "/liked" },
    { divider: true },
    { icon: "🔥", label: "ট্রেন্ডিং", path: "/trending", section: "এক্সপ্লোর" },
    { icon: "🛍️", label: "শপিং", path: "/shopping" },
    { icon: "🎵", label: "মিউজিক", path: "/music" },
    { icon: "🎮", label: "গেমিং", path: "/gaming" },
    { icon: "📰", label: "খবর", path: "/news" },
  ];

  if (collapsed) {
    return (
      <aside className="w-[72px] bg-[#0f0f0f] h-[calc(100vh-56px)] sticky top-[56px] py-3 hidden md:block overflow-y-auto scrollbar-hide">
        <nav className="flex flex-col gap-1">
          <Link to="/" className={`flex flex-col items-center gap-1 p-4 rounded-xl hover:bg-[#272727] ${isActive('/') ? 'bg-[#272727]' : ''}`}>
            <span className="text-xl">🏠</span>
            <span className="text-[10px]">হোম</span>
          </Link>
          <Link to="/shorts" className={`flex flex-col items-center gap-1 p-4 rounded-xl hover:bg-[#272727] ${isActive('/shorts') ? 'bg-[#272727]' : ''}`}>
            <span className="text-xl">🎬</span>
            <span className="text-[10px]">Shorts</span>
          </Link>
          <Link to="/subscriptions" className="flex flex-col items-center gap-1 p-4 rounded-xl hover:bg-[#272727]">
            <span className="text-xl">📺</span>
            <span className="text-[10px] text-center leading-tight">সাবস্ক্রিপশন</span>
          </Link>
          <Link to="/you" className="flex flex-col items-center gap-1 p-4 rounded-xl hover:bg-[#272727]">
            <span className="text-xl">📚</span>
            <span className="text-[10px]">আপনি</span>
          </Link>
        </nav>
      </aside>
    );
  }

  return (
    <aside className="w-[240px] bg-[#0f0f0f] h-[calc(100vh-56px)] sticky top-[56px] overflow-y-auto scrollbar-hide hidden md:block">
      <nav className="p-3">
        {menuItems.map((item, idx) => {
          if (item.divider) return <hr key={idx} className="my-3 border-[#303030]" />;
          return (
            <div key={idx}>
              {item.section && <h3 className="px-3 py-2 font-medium text-[16px]">{item.section}</h3>}
              <Link
                to={item.path}
                className={`flex items-center gap-6 px-3 py-2 rounded-xl hover:bg-[#272727] text-sm ${isActive(item.path) ? 'bg-[#272727] font-medium' : ''}`}
              >
                <span className="text-xl">{item.icon}</span>
                <span>{item.label}</span>
              </Link>
            </div>
          );
        })}

        <div className="mt-6 px-3 text-[12px] text-[#aaa] leading-5">
          <p className="font-medium mb-2">আমিনের জন্য তৈরি ❤️</p>
          <p>MyTube - অ্যান্ড্রয়েড ডেভেলপারদের জন্য শিক্ষামূলক প্ল্যাটফর্ম</p>
          <div className="flex flex-wrap gap-2 mt-3">
            <span>About</span><span>Press</span><span>Copyright</span><span>Contact</span>
          </div>
          <p className="mt-3 text-[11px]">© 2025 MyTube Bangladesh</p>
        </div>
      </nav>
    </aside>
  );
}
