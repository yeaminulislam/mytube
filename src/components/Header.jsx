import { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getSearchSuggestions } from '../services/youtubeApi';

export default function Header({ onSearch, searchQuery, onToggleSidebar }) {
  const [inputValue, setInputValue] = useState(searchQuery || '');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const searchRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (searchRef.current && !searchRef.current.contains(e.target)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Debounced suggestions - YouTube Autocomplete
  useEffect(() => {
    if (!inputValue.trim()) {
      setSuggestions([]);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const suggs = await getSearchSuggestions(inputValue);
        setSuggestions(suggs);
      } catch (e) {
        console.error('Suggestions error:', e);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [inputValue]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!inputValue.trim()) return;
    setShowSuggestions(false);
    onSearch(inputValue);
    navigate(`/?q=${encodeURIComponent(inputValue)}`);
  };

  const handleSuggestionClick = (suggestion) => {
    setInputValue(suggestion);
    setShowSuggestions(false);
    onSearch(suggestion);
    navigate(`/?q=${encodeURIComponent(suggestion)}`);
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

      {/* Center - Search with Suggestions */}
      <form onSubmit={handleSearch} className="flex-1 max-w-[600px] flex" ref={searchRef}>
        <div className="flex flex-1 relative">
          <div className="flex flex-1 relative">
            <input
              type="text"
              value={inputValue}
              onChange={(e) => {
                setInputValue(e.target.value);
                setShowSuggestions(true);
              }}
              onFocus={() => inputValue && setShowSuggestions(true)}
              placeholder="সার্চ করুন"
              className="w-full bg-[#121212] border border-[#303030] rounded-l-full px-5 py-[10px] text-[16px] focus:outline-none focus:border-[#1c62b9] placeholder:text-[#888]"
            />
            {/* Suggestions Dropdown - Like Real YouTube */}
            {showSuggestions && suggestions.length > 0 && (
              <div className="absolute top-full left-0 right-0 mt-1 bg-[#212121] rounded-xl shadow-2xl border border-[#303030] overflow-hidden z-50">
                <div className="py-2">
                  {suggestions.map((sugg, idx) => (
                    <button
                      key={idx}
                      type="button"
                      onClick={() => handleSuggestionClick(sugg)}
                      className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4 text-sm"
                    >
                      <svg className="w-4 h-4 text-[#717171]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                      </svg>
                      {sugg}
                    </button>
                  ))}
                </div>
                <div className="px-4 py-2 border-t border-[#303030] text-xs text-[#717171]">
                  💡 Real YouTube Suggest API: <code className="bg-[#303030] px-1 rounded">suggestqueries.google.com</code>
                </div>
              </div>
            )}
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
              <div className="absolute right-0 top-12 w-80 bg-[#212121] rounded-xl shadow-2xl overflow-hidden z-50 border border-[#303030]">
                <div className="p-4 flex gap-3 border-b border-[#303030]">
                  <img src={user.avatar} className="w-10 h-10 rounded-full" alt="" />
                  <div className="flex-1 min-w-0">
                    <p className="font-medium truncate">{user.name}</p>
                    <p className="text-sm text-[#aaa] truncate">{user.email}</p>
                    <p className="text-xs mt-1">
                      <span className={`px-2 py-0.5 rounded-full ${user.provider === 'google' ? 'bg-blue-900/30 text-blue-400 border border-blue-800' : 'bg-[#303030] text-[#aaa]'}`}>
                        {user.provider === 'google' ? '🔐 Google OAuth' : '📧 Email'}
                      </span>
                      {user.idToken && <span className="ml-1 bg-green-900/30 text-green-400 px-1.5 py-0.5 rounded-full text-[10px]">JWT ✓</span>}
                    </p>
                  </div>
                </div>
                <div className="py-2">
                  <div className="px-4 py-2 bg-[#181818] m-2 rounded-lg text-xs">
                    <p className="text-[#aaa] font-medium">🔑 Auth Debug:</p>
                    <p className="text-[#717171] mt-1 font-mono text-[11px] break-all">ID: {user.id?.substring(0, 20)}...</p>
                    <p className="text-[#717171] font-mono text-[11px]">Provider: {user.provider}</p>
                    <p className="text-[#717171] font-mono text-[11px]">Joined: {new Date(user.joinedAt).toLocaleDateString()}</p>
                  </div>
                  <button className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4 text-sm">
                    <span>👤</span> আপনার চ্যানেল
                  </button>
                  <button className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4 text-sm">
                    <span>⚙️</span> সেটিংস
                  </button>
                  <button onClick={() => { logout(); setShowUserMenu(false); }} className="w-full text-left px-4 py-2 hover:bg-[#303030] flex items-center gap-4 border-t border-[#303030] mt-2 text-sm">
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
