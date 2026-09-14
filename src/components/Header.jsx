import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Header({ onSearch, searchQuery, onToggleSidebar }) {
  const [inputValue, setInputValue] = useState(searchQuery || '');
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(inputValue);
    navigate(`/?q=${encodeURIComponent(inputValue)}`);
  };

  return (
    <header className="sticky top-0 z-50 bg-[#0f0f0f] flex items-center justify-between px-4 py-2 gap-4">
      {/* Left */}
      <div className="flex items-center gap-4">
        <button onClick={onToggleSidebar} className="p-2 hover:bg-[#272727] rounded-full">
          <svg className="w-6 h-6" fill="white" viewBox="0 0 24 24">
            <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z" />
          </svg>
        </button>
        <Link to="/" className="flex items-center gap-1">
          <div className="bg-red-600 rounded-lg p-1.5">
            <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="currentColor">
              <path d="M8 5.14v14l11-7-11-7z" />
            </svg>
          </div>
          <span className="text-xl font-bold tracking-tighter">MyTube</span>
          <sup className="text-[10px] text-[#aaa] ml-1 font-normal">BD</sup>
        </Link>
      </div>

      {/* Center - Search */}
      <form onSubmit={handleSearch} className="flex-1 max-w-[600px] flex">
        <div className="flex flex-1">
          <div className="flex flex-1 relative">
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder="সার্চ করুন"
              className="w-full bg-[#121212] border border-[#303030] rounded-l-full px-5 py-[10px] text-[16px] focus:outline-none focus:border-[#1c62b9] placeholder:text-[#888]"
            />
          </div>
          <button type="submit" className="bg-[#222222] border border-l-0 border-[#303030] rounded-r-full px-6 hover:bg-[#272727]">
            <svg className="w-5 h-5" fill="none" stroke="white" strokeWidth="2" viewBox="0 0 24 24">
              <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </button>
        </div>
        <button type="button" className="ml-3 bg-[#272727] p-3 rounded-full hover:bg-[#3f3f3f]">
          <svg className="w-5 h-5" fill="white" viewBox="0 0 24 24">
            <path d="M12 14c1.66 0 3-1.34 3-3V5c0-1.66-1.34-3-3-3S9 3.34 9 5v6c0 1.66 1.34 3 3 3z" />
            <path d="M17 11c0 2.76-2.24 5-5 5s-5-2.24-5-5H5c0 3.53 2.61 6.43 6 6.92V21h2v-3.08c3.39-.49 6-3.39 6-6.92h-2z" />
          </svg>
        </button>
      </form>

      {/* Right */}
      <div className="flex items-center gap-2">
        <button className="hidden md:block p-2 hover:bg-[#272727] rounded-full">
          <svg className="w-6 h-6" fill="none" stroke="white" strokeWidth="1.5" viewBox="0 0 24 24">
            <path d="M14.5 4h-5L7 7v10l2.5 3h5L17 17V7l-2.5-3z" />
            <path d="M12 8v8M8 12h8" />
          </svg>
        </button>
        <button className="hidden md:block p-2 hover:bg-[#272727] rounded-full relative">
          <svg className="w-6 h-6" fill="white" viewBox="0 0 24 24">
            <path d="M12 22c1.1 0 2-.9 2-2h-4a2 2 0 002 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
          </svg>
          <span className="absolute -top-1 -right-1 bg-red-600 text-white text-[11px] px-1.5 rounded-full">9+</span>
        </button>

        {user ? (
          <div className="relative">
            <button onClick={() => setShowUserMenu(!showUserMenu)} className="ml-2">
              <img src={user.avatar} alt={user.name} className="w-8 h-8 rounded-full" />
            </button>
            {showUserMenu && (
              <div className="absolute right-0 top-12 w-72 bg-[#212121] rounded-xl shadow-2xl overflow-hidden z-50">
                <div className="p-4 flex gap-3 border-b border-[#303030]">
                  <img src={user.avatar} className="w-10 h-10 rounded-full" alt="" />
                  <div>
                    <p className="font-medium">{user.name}</p>
                    <p className="text-sm text-[#aaa]">{user.email}</p>
                    <Link to="#" className="text-sm text-[#3ea6ff] mt-1 block">Google অ্যাকাউন্ট পরিচালনা করুন</Link>
                  </div>
                </div>
                <div className="py-2">
                  <button className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4">
                    <span>👤</span> আপনার চ্যানেল
                  </button>
                  <button className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4">
                    <span>💰</span> কেনাকাটা এবং সদস্যতা
                  </button>
                  <button className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4">
                    <span>⚙️</span> সেটিংস
                  </button>
                  <button onClick={() => { logout(); setShowUserMenu(false); }} className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4 border-t border-[#303030] mt-2">
                    <span>🚪</span> লগআউট
                  </button>
                </div>
              </div>
            )}
          </div>
        ) : (
          <Link to="/login" className="flex items-center gap-2 border border-[#303030] rounded-full px-4 py-1.5 hover:bg-[#263850] hover:border-[#263850] text-[#3ea6ff]">
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
            </svg>
            <span className="text-sm font-medium">লগইন</span>
          </Link>
        )}
      </div>
    </header>
  );
}
