import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Home from './pages/Home';
import VideoPage from './pages/VideoPage';
import ShortsPage from './pages/ShortsPage';
import Login from './pages/Login';
import ApiDebugger from './pages/ApiDebugger';
import InstallPage from './pages/InstallPage';
import VancedPage from './pages/VancedPage';
import { AuthProvider } from './context/AuthContext';

function AppContent() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const location = useLocation();

  const isShortsPage = location.pathname === '/shorts';
  const isLoginPage = location.pathname === '/login';
  const isVideoPage = location.pathname.startsWith('/watch');

  const showSidebar = !isShortsPage && !isLoginPage;

  return (
    <div className="min-h-screen bg-[#0f0f0f] text-white">
      <Header
        onSearch={setSearchQuery}
        searchQuery={searchQuery}
        onToggleSidebar={() => setSidebarCollapsed(!sidebarCollapsed)}
      />
      <div className="flex">
        {showSidebar && <Sidebar collapsed={sidebarCollapsed || isVideoPage} />}
        
        {/* Mobile Bottom Nav */}
        <div className="md:hidden fixed bottom-0 left-0 right-0 bg-[#0f0f0f] border-t border-[#303030] flex justify-around py-2 z-50">
          <a href="/" className="flex flex-col items-center gap-1 p-2">
            <span>🏠</span><span className="text-[10px]">হোম</span>
          </a>
          <a href="/shorts" className="flex flex-col items-center gap-1 p-2">
            <span>🎬</span><span className="text-[10px]">Shorts</span>
          </a>
          <a href="#" className="flex flex-col items-center gap-1 p-2">
            <span className="w-6 h-6 bg-white text-black rounded-full flex items-center justify-center">+</span>
          </a>
          <a href="#" className="flex flex-col items-center gap-1 p-2">
            <span>📺</span><span className="text-[10px]">সাবস্ক্রিপশন</span>
          </a>
          <a href="/login" className="flex flex-col items-center gap-1 p-2">
            <span>👤</span><span className="text-[10px]">আপনি</span>
          </a>
        </div>

        <main className={`flex-1 ${isShortsPage ? '' : 'pb-20 md:pb-0'}`}>
          <Routes>
            <Route path="/" element={<Home searchQuery={searchQuery} />} />
            <Route path="/watch/:id" element={<VideoPage />} />
            <Route path="/shorts" element={<ShortsPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/debug" element={<ApiDebugger />} />
            <Route path="/install" element={<InstallPage />} />
            <Route path="/vanced" element={<VancedPage />} />
            <Route path="*" element={
              <div className="flex items-center justify-center h-[calc(100vh-56px)] flex-col gap-4 p-6 text-center">
                <p className="text-6xl">🚧</p>
                <p className="text-xl">এই পেজটি শীঘ্রই আসছে!</p>
                <p className="text-[#aaa]">আমিন, এটি তোমার পরবর্তী ফিচার হতে পারে।</p>
                <div className="flex gap-2 mt-4 flex-wrap justify-center">
                  <a href="/" className="bg-white text-black px-6 py-2 rounded-full font-medium">হোমে ফিরুন</a>
                  <a href="/install" className="bg-red-600 text-white px-6 py-2 rounded-full font-medium">📱 ফোনে ইন্সটল</a>
                  <a href="/debug" className="bg-[#272727] text-white px-6 py-2 rounded-full font-medium">🔧 API Debugger</a>
                </div>
              </div>
            } />
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}
